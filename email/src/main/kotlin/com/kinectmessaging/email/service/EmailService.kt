package com.kinectmessaging.email.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.kinectmessaging.email.com.kinectmessaging.email.client.ContactHistoryClient
import com.kinectmessaging.email.com.kinectmessaging.email.client.TemplateClient
import com.kinectmessaging.libs.common.EmailUtils
import com.kinectmessaging.libs.model.*
import io.cloudevents.core.builder.CloudEventBuilder
import io.cloudevents.core.data.PojoCloudEventData
import io.cloudevents.core.format.ContentType
import io.cloudevents.core.provider.EventFormatProvider
import io.quarkus.logging.Log
import io.vertx.ext.mail.MailMessage
import io.vertx.mutiny.ext.mail.MailClient
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import java.net.URI
import java.time.LocalDateTime
import java.time.OffsetDateTime


@ApplicationScoped
class EmailService(
    @ConfigProperty(name = "app.client.template.url")
    val templateClientBaseUrl: String,
    @ConfigProperty(name = "app.client.contact-history.url")
    val contactHistoryClientBaseUrl: String,
    @ConfigProperty(name = "quarkus.mailer.from")
    var senderAddress: String,
) {

    @Inject
    lateinit var mailClient: MailClient

    @Inject
    lateinit var mapper: ObjectMapper

    @Inject
    @RestClient
    lateinit var templateClient: TemplateClient

    @Inject
    @RestClient
    lateinit var contactHistoryClient: ContactHistoryClient


    fun deliverEmail(kMessage: KMessage): String? {
        kMessage.emailData?.let { emailData ->
            val toRecipients = mapRecipients(emailData.toRecipients)
            val ccRecipients = emailData.ccRecipients?.let { mapRecipients(it) }
            val bccRecipients = emailData.bccRecipients?.let { mapRecipients(it) }
            val subject = emailData.subject
            val templates =
                templateClient.loadTemplate(
                    templateClientBaseUrl,
                    TemplatePersonalizationRequest(
                        textTemplateId = emailData.textTemplateId,
                        htmlTemplateId = emailData.htmlTemplateId,
                        personalizationData = emailData.personalizationData
                    )
                )

            val plainEmailBody = templates?.first { it.templateId == emailData.textTemplateId }?.templateContent
            val htmlEmailMessage = templates?.first { it.templateId == emailData.htmlTemplateId }?.templateContent
            senderAddress = if (EmailUtils.isEmailValid(emailData.senderAddress)){
                emailData.senderAddress
            } else {
                senderAddress
            }
            
            val result = if (plainEmailBody?.isNotBlank() == true || htmlEmailMessage?.isNotBlank() == true) {
                val message = MailMessage()
                message.from = senderAddress
                message.subject = subject
                message.to = toRecipients
                message.cc = ccRecipients
                message.bcc = bccRecipients
                message.text = plainEmailBody
                message.html = htmlEmailMessage
                Log.debug("Created Azure Email Message : $message")

                val result = mailClient.sendMailAndAwait(message)
                Log.debug("Sent email to recipient - ${message.to}")

                val contactMessages = ContactMessages(
                    messageId = kMessage.id,
                    deliveryTrackingId = result.messageID,
                    deliveryChannel = DeliveryChannel.EMAIL,
                    contactAddress = message.to[0],
                    deliveryStatus = listOf(
                        DeliveryStatus(
                            statusTime = LocalDateTime.now(),
                            status = HistoryStatusCodes.SENT,
                            statusMessage = null,
                            originalStatus = null
                        )
                    ),
                    engagementStatus = null
                )
                contactHistoryClient.updateContactMessages(contactHistoryClientBaseUrl, contactMessages.messageId, OffsetDateTime.now().toString(), contactMessages )
                Log.info("Updating contact history from Azure Email Service for id - ${contactMessages.messageId} and Delivery Tracking id - ${result.messageID}")

                "Delivery Tracking id - ${result.messageID}"
            } 
            else{
                "No Template rendered"
            }
            return result
            
        } ?: throw RuntimeException("Unable to send email. Email data is empty.")
    }

    private fun mapRecipients(recipients: List<Person>): List<String> {
        val messageRecipients = mutableListOf<String>()
        recipients.forEach { recipient ->
            recipient.contacts?.forEach { contact ->
                contact.email?.let { messageRecipients.add(it) }
            }
        }
        return messageRecipients
    }
}