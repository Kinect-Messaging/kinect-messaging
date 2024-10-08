package com.kinectmessaging.ep.service

import com.dashjoin.jsonata.Jsonata.jsonata
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kinectmessaging.ep.client.ApiClient
import com.kinectmessaging.libs.exception.InvalidInputException
import com.kinectmessaging.libs.model.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*
import javax.mail.internet.InternetAddress

@Service
class EventProcessorService {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var apiClient: ApiClient

    suspend fun processEvent(event: KEvent): String{
        val result: String

        // Throw error if both payload and recipients are empty
        if (event.payload?.isNull == true && event.recipients?.isEmpty() == true){
            throw InvalidInputException(message = "Payload and recipients are empty in the event. Fix event data and retry.")
        }

        // Get matching configurations for the event
        val notificationMessages = mutableListOf<KMessage>()
        val matchingJourneys = apiClient.getJourneyConfigsByEventName(eventName = event.eventName)
        val payload = event.payload?.let { jacksonObjectMapper().convertValue<Map<String, Any>>(it) }

        matchingJourneys?.forEach { journeyConfig ->
            val messageConfigs = mutableListOf<MessageConfig>()
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
                        apiClient.getMessageConfigsById(key)?.let { messageConfigs.add(it) }
                    }
                }
            }

            // Create relevant notification from configs
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
                        val toRecipients = evaluateEmailRecipients(emailConfig.toRecipients, payload)
                        val ccRecipients = evaluateEmailRecipients(emailConfig.ccRecipients, payload)
                        val bccRecipients = evaluateEmailRecipients(emailConfig.bccRecipients, payload)
                        val replyTo = evaluateEmailRecipients(emailConfig.replyTo, payload)
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
                        notificationMessages.add(notificationMessage)
                    }
                }
            }
        }

        // Invoke the relevant target service for each notification
        notificationMessages.forEach { notificationMessage ->
            apiClient.sendEmail(notificationMessage)
        }
        result = "Total notifications sent ${notificationMessages.size}"
        return result
    }

    private fun evaluateEmailRecipients(recipientConfigs: List<EmailRecipientConfig>?, payload: Map<String, Any>?): List<InternetAddress>{
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

    private fun evaluatePersonalizationData(personalizationData: Map<String, Map<String, String?>?>?, payload: Map<String, Any>?): Map<String, Map<String, String?>?>{
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