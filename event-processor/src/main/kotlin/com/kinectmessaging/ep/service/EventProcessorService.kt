package com.kinectmessaging.ep.service

import com.dashjoin.jsonata.Jsonata.jsonata
import com.fasterxml.jackson.databind.ObjectMapper
import com.kinectmessaging.ep.client.ConfigClient
import com.kinectmessaging.ep.client.ContactHistoryClient
import com.kinectmessaging.ep.client.NotificationClient
import com.kinectmessaging.libs.common.LogConstants
import com.kinectmessaging.libs.exception.InvalidInputException
import com.kinectmessaging.libs.model.*
import io.cloudevents.core.builder.CloudEventBuilder
import io.cloudevents.core.data.PojoCloudEventData
import io.cloudevents.core.format.ContentType
import io.cloudevents.core.provider.EventFormatProvider
import io.quarkus.logging.Log
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.mail.internet.InternetAddress
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import java.net.URI
import java.time.LocalDateTime
import java.util.*

@ApplicationScoped
class EventProcessorService(
    @ConfigProperty(name = "app.client.contact-history.url")
    val contactHistoryClientBaseUrl: String,
    @ConfigProperty(name = "app.client.notification.url")
    val notificationClientBaseUrl: String,
    @ConfigProperty(name = "app.client.config.journey.url")
    val journeyClientBaseUrl: String,
    @ConfigProperty(name = "app.client.config.message.url")
    val messageClientBaseUrl: String,
    @ConfigProperty(name = "app.cloud-events.headers.spec-version")
    val cloudEventsSpecVersion: String,
    @ConfigProperty(name = "app.cloud-events.headers.contact-history.type")
    val contactHistoryCloudEventsType: String,
    @ConfigProperty(name = "app.cloud-events.headers.contact-history.source")
    val contactHistoryCloudEventsSource: String,
    @ConfigProperty(name = "app.cloud-events.headers.notification.type")
    val notificationCloudEventsType: String,
    @ConfigProperty(name = "app.cloud-events.headers.notification.source")
    val notificationCloudEventsSource: String,
) {

    @Inject
    private lateinit var mapper: ObjectMapper

    @Inject
    @field:RestClient
    private lateinit var configClient: ConfigClient

    @Inject
    @field:RestClient
    private lateinit var contactHistoryClient: ContactHistoryClient

    @Inject
    @field:RestClient
    private lateinit var notificationClient: NotificationClient

    /**
     * @author Raj Palanisamy
     * Function to process the incoming event and convert to a list of notifications to be sent via different channels. Upon successful invocation of each target, returns a count of notifications processed.
     * @return Total number of notifications processed.
     */
    fun processEvent(event: KEvent): String{
        val result: String

        // Throw error if both payload and recipients are empty
        if (event.payload == null && event.recipients?.isEmpty() == true){
            throw InvalidInputException(message = "Payload and recipients are empty in the event. Fix event data and retry.")
        }

        // Get matching configurations for the event
        val notificationMessages = mutableListOf<KMessage>()
        val contactHistoryList = mutableListOf<KContactHistory>()
        Log.debug("${LogConstants.SERVICE_DEBUG} Calling Journey configs for event ${event.eventName} with id ${event.eventId}")
        val matchingJourneys = configClient.getJourneyConfigsByEventName("$journeyClientBaseUrl${event.eventName}")
        Log.debug("${LogConstants.SERVICE_DEBUG} Fetched Journey configs for event ${event.eventName} with id ${event.eventId} - $matchingJourneys")
        val payload: Map<*, *>? = mapper.convertValue(event.payload, Map::class.java)
        Log.debug("${LogConstants.SERVICE_DEBUG} Payload for jsonata evaluation - $payload")
        matchingJourneys?.forEach { journeyConfig ->
            val messageConfigs = mutableListOf<MessageConfig>()
            val journeyTransactionId = UUID.randomUUID().toString()
            val journeySteps = journeyConfig.journeySteps
            journeySteps?.filter { it.eventName == event.eventName }?.forEach { journeyStep ->

                // verify if step condition exists and evaluates to true
                val stepConditionResult =
                    when(journeyStep.stepCondition?.isNotBlank()){
                        true -> {
                            jsonata(journeyStep.stepCondition).evaluate(payload) as Boolean
                        }
                        false, null -> true
                    }

                // Get message configurations for each id
                if (stepConditionResult){
                    journeyStep.messageConfigs.forEach { (key, _) ->
                        configClient.getMessageConfigsById("${messageClientBaseUrl}/$key")?.let { messageConfigs.add(it) }
                    }
                }
            } ?: Log.warn("No valid journey steps for event ${event.eventName} and journey ${journeyConfig.journeyName}")

            Log.debug("${LogConstants.SERVICE_DEBUG} Fetched Message configs for event ${event.eventName} with id ${event.eventId} - $messageConfigs")
            // Create relevant notification from configs
            if (messageConfigs.isEmpty()){
                Log.warn("No valid message configs fetched for event ${event.eventName} and journey ${journeyConfig.journeyName}")
            }
            messageConfigs.forEach { messageConfig ->
                // verify if message condition exists and evaluates to true
                val messageConditionResult =
                    when(messageConfig.messageCondition?.isNotBlank()){
                        true -> {
                            jsonata(messageConfig.messageCondition).evaluate(payload) as Boolean
                        }
                        false, null -> true
                    }

                // Evaluate Email Config
                if (messageConditionResult){
                    messageConfig.emailConfig?.forEach { emailConfig ->
                        val textTemplateId = emailConfig.templateConfig.filterValues { it == "text" }.keys.first()
                        val htmlTemplateId = emailConfig.templateConfig.filterValues { it == "html" }.keys.first()
                        val senderAddress = emailConfig.senderAddress ?: ""
                        val subject = jsonata(emailConfig.subject).evaluate(payload).toString()
                        val toRecipients = if (event.recipients?.isNotEmpty() == true){
                            evaluateEmailRecipientsFromPayload(event.recipients)
                        } else {
                            evaluateEmailRecipientsFromEmailConfig(emailConfig.toRecipients, payload)
                        }
                        val ccRecipients = evaluateEmailRecipientsFromEmailConfig(emailConfig.ccRecipients, payload)
                        val bccRecipients = evaluateEmailRecipientsFromEmailConfig(emailConfig.bccRecipients, payload)
                        val replyTo = evaluateEmailRecipientsFromEmailConfig(emailConfig.replyTo, payload)
                        val personalizationData = evaluatePersonalizationData(emailConfig.personalizationData, payload)
                        val notificationMessage = KMessage(
                            id = UUID.randomUUID().toString(),
                            sourceId = event.eventId,
                            deliveryChannel = DeliveryChannel.EMAIL,
                            targetSystem = emailConfig.targetSystem,
                            emailData = EmailData(
                                emailHeaders = emailConfig.emailHeaders,
                                textTemplateId = textTemplateId,
                                htmlTemplateId = htmlTemplateId,
                                senderAddress = senderAddress,
                                subject = subject,
                                toRecipients = toRecipients,
                                ccRecipients = ccRecipients,
                                bccRecipients = bccRecipients,
                                attachments = null,
                                replyTo = replyTo,
                                personalizationData = personalizationData
                            )
                        )

                        // Add Contact History record
                        toRecipients.forEach { recipient ->
                            contactHistoryList.add(
                                KContactHistory(
                                    id = UUID.randomUUID().toString(),
                                    sourceEventId = event.eventId,
                                    journeyTransactionId = journeyTransactionId,
                                    journeyName = journeyConfig.journeyName,
                                    messages = ContactMessages(
                                        messageId = notificationMessage.id,
                                        deliveryTrackingId = null,
                                        deliveryChannel = notificationMessage.deliveryChannel,
                                        contactAddress = recipient.address,
                                        deliveryStatus = mutableListOf(
                                            DeliveryStatus(
                                                statusTime = LocalDateTime.now(),
                                                status = HistoryStatusCodes.CREATED,
                                                statusMessage = null,
                                                originalStatus = null,
                                            )
                                        ),
                                        engagementStatus = null,
                                    ),
                                )
                            )
                        }

                        notificationMessages.add(notificationMessage)
                    }
                }
            }
        }


        Log.debug("${LogConstants.SERVICE_DEBUG} Updating Contact History records for event ${event.eventName} with id ${event.eventId} - $contactHistoryList ")
        // Publish contact history records
        contactHistoryList.forEach { contactHistory ->
            Log.debug("Updating Contact History ${contactHistory.id}")
            val contactHistoryEvent = CloudEventBuilder.v1()
                .withSource(URI.create(contactHistoryCloudEventsSource))
                .withType(contactHistoryCloudEventsType)
                .withId(contactHistory.id)
                .withDataContentType(MediaType.APPLICATION_JSON)
                .withData(PojoCloudEventData.wrap(contactHistory, mapper::writeValueAsBytes))
                .build()

            val serialized: ByteArray = EventFormatProvider
                .getInstance()
                .resolveFormat(ContentType.JSON)
                ?.serialize(contactHistoryEvent) ?: throw BadRequestException("Unable to serialize cloud event data $contactHistoryEvent")
            contactHistoryClient.createContactHistory(contactHistoryClientBaseUrl, serialized)
        }

        Log.debug("${LogConstants.SERVICE_DEBUG} Publishing notification messages to delivery channels for event ${event.eventName} with id ${event.eventId} - $notificationMessages")
        // Invoke the relevant target service for each notification
        notificationMessages.forEach { notificationMessage ->
            val notificationEvent = CloudEventBuilder.v1()
                .withSource(URI.create(notificationCloudEventsSource))
                .withType(notificationCloudEventsType)
                .withId(notificationMessage.id)
                .withDataContentType(MediaType.APPLICATION_JSON)
                .withData(PojoCloudEventData.wrap(notificationMessage, mapper::writeValueAsBytes))
                .build()

            val serialized: ByteArray = EventFormatProvider
                .getInstance()
                .resolveFormat(ContentType.JSON)
                ?.serialize(notificationEvent) ?: throw BadRequestException("Unable to serialize cloud event data $notificationEvent")
            when(notificationMessage.deliveryChannel){
                DeliveryChannel.EMAIL -> {
                    notificationClient.sendNotification(
                        notificationClientBaseUrl,
                        serialized
                    )
                }
                else -> {
                    Log.warn("No valid delivery channel found in notification message for event ${event.eventName} with id ${event.eventId} - $notificationMessage")
                }
            }

        }

        result = "Total notifications sent - ${notificationMessages.size}"
        return result
    }


    /**
     * @author Raj Palanisamy
     * Function to parse the recipients of each email configurations per message configuration and evaluate the recipients. Uses Jsonata evaluation to convert the configurations to actual values and maps to a list of InternetAddress.
     * @return List<InternetAddress>
     */
    private fun evaluateEmailRecipientsFromEmailConfig(recipientConfigs: List<EmailRecipientConfig>?, payload: Map<*, *>?): List<InternetAddress>{
        val recipients = mutableListOf<InternetAddress>()
        recipientConfigs?.forEach { recipientConfig ->
            val emailAddress = recipientConfig.emailAddress
            if (emailAddress.isNotBlank()) {
                recipients.add(
                    InternetAddress(
                        jsonata(recipientConfig.emailAddress).evaluate(payload).toString(),
                        jsonata(recipientConfig.firstName).evaluate(payload).toString() + ", " +  jsonata(recipientConfig.lastName).evaluate(payload).toString()
                    )
                )
            }

        }
        return recipients
    }

    /**
     * @author Raj Palanisamy
     * Function to retrieve the recipients from payload and convert to a list of InternetAddress.
     * @return List<InternetAddress>
     */
    private fun evaluateEmailRecipientsFromPayload(recipientConfigs: List<Person>?): List<InternetAddress>{
        val recipients = mutableListOf<InternetAddress>()
        recipientConfigs?.forEach { recipientConfig ->
            recipientConfig.contacts?.forEach { contact ->
                val emailAddress = contact.email
                if (emailAddress?.isNotBlank() == true) {
                    recipients.add(
                        InternetAddress(
                            emailAddress,
                            recipientConfig.lastName?.let { recipientConfig.firstName + ", " + recipientConfig.lastName } ?: recipientConfig.firstName
                        )
                    )
                }
            }
        }
        return recipients
    }

    /**
     * @author Raj Palanisamy
     * Function to parse the data from payload that would be required to personalize the notifications. Uses Jsonata evaluation to convert the configurations to actual values and returns a map of a map of attributes.
     * @return Map<String, Map<String, String?>?>
     */
    private fun evaluatePersonalizationData(personalizationData: Map<String, Map<String, String?>?>?, payload: Map<*, *>?): Map<String, Map<String, String?>?>{
        val resultPersonalizationData = mutableMapOf<String, MutableMap<String, String?>?>()
        personalizationData?.forEach { (objectName, attributesMap) ->
            val resultAttributeMap = mutableMapOf<String, String?>()
            attributesMap?.forEach { (name, value) ->
                resultAttributeMap[name] = jsonata(value).evaluate(payload).toString()
            }
            resultPersonalizationData [objectName] = resultAttributeMap
        }
        return resultPersonalizationData
    }

}

