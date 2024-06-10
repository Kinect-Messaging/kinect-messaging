package com.kinect.messaging.email.service

import com.azure.communication.email.EmailClientBuilder
import com.azure.communication.email.models.EmailAddress
import com.azure.communication.email.models.EmailMessage
import com.azure.communication.email.models.EmailSendResult
import com.azure.core.util.polling.LongRunningOperationStatus
import com.azure.core.util.polling.SyncPoller
import com.kinect.messaging.email.client.TemplateClient
import com.kinect.messaging.libs.model.KMessage
import com.kinect.messaging.libs.model.TemplatePersonalizationRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import javax.mail.internet.InternetAddress


@Service
class AzureEmailService : EmailService {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Value("\${app.email.azure.connectionString}")
    lateinit var azureConnectionString: String

    @Value("\${app.email.azure.defaults.senderAddress}")
    lateinit var senderAddress: String

    @Value("\${app.feature-flag.send-email}")
    var sendEmail: Boolean = false

    @Autowired
    lateinit var templateClient: TemplateClient

    override suspend fun deliverEmail(kMessage: KMessage): String? {
        kMessage.emailData?.let { emailData ->
            val toRecipients = mapRecipients(emailData.toRecipients)
            val ccRecipients = emailData.ccRecipients?.let { mapRecipients(it) }
            val bccRecipients = emailData.bccRecipients?.let { mapRecipients(it) }
            val subject = emailData.subject
            val templates = withContext(MDCContext()){
                templateClient.loadTemplate(
                    TemplatePersonalizationRequest(
                        textTemplateId = emailData.textTemplateId,
                        htmlTemplateId = emailData.htmlTemplateId,
                        personalizationData = emailData.personalizationData
                    )
                )
            }

            val plainEmailBody = templates?.first { it.templateId == emailData.textTemplateId }?.templateContent
            val htmlEmailMessage = templates?.first { it.templateId == emailData.htmlTemplateId }?.templateContent

            if (plainEmailBody?.isNotBlank() == true || htmlEmailMessage?.isNotBlank() == true) {
                val message = EmailMessage()
                    .setSenderAddress(senderAddress)
                    .setToRecipients(toRecipients)
                    .setCcRecipients(ccRecipients)
                    .setBccRecipients(bccRecipients)
                    .setSubject(subject)
                    .setBodyPlainText(plainEmailBody)
                    .setBodyHtml(htmlEmailMessage)

                if (sendEmail) {
                    return sendEmailWithAzure(message)
                }
                log.info("Created Azure Email Message : ${message.bodyHtml}")
            }

            return htmlEmailMessage ?: "No Template rendered"
        } ?: throw RuntimeException("Unable to send email. Email data is empty.")
    }

    suspend fun sendEmailWithAzure(message: EmailMessage): String? {
        try {
            val result = withContext(Dispatchers.IO) {
                val emailClient = EmailClientBuilder()
                    .connectionString(azureConnectionString)
                    .buildClient()
                val poller: SyncPoller<EmailSendResult, EmailSendResult> = emailClient.beginSend(message, null)
                val result = poller.waitForCompletion()
                if (result.status == LongRunningOperationStatus.SUCCESSFULLY_COMPLETED){
                    val emailResult = result.value
                    log.info("Result from Azure Email Service - ${emailResult.status} for id - ${emailResult.id}")
                    return@withContext emailResult.status.toString()
                } else {
                    log.error("Result from Azure Email Service - ${result.status}")
                    return@withContext result.status.toString()
                }

            }
            return result

        } catch (e: Throwable) {
            log.error(e.message)
            return "Unexpected error while sending message ${e.message}"
        }
    }

    private fun mapRecipients(recipients: List<InternetAddress>): List<EmailAddress> {
        val azureRecipients = mutableListOf<EmailAddress>()
        recipients.forEach {
            val emailAddress = EmailAddress(it.address)
            emailAddress.displayName = it.personal
            azureRecipients.add(emailAddress)
        }
        return azureRecipients
    }

}