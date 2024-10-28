package com.kinectmessaging.ch.service

import com.azure.spring.integration.storage.queue.inbound.StorageQueueMessageSource
import com.azure.spring.messaging.AzureHeaders
import com.azure.spring.messaging.checkpoint.Checkpointer
import com.azure.storage.queue.QueueClient
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.kinectmessaging.ch.model.AzureEmailDeliveryReport
import com.kinectmessaging.ch.model.AzureEmailDeliveryStatus
import com.kinectmessaging.libs.common.LogConstants
import com.kinectmessaging.libs.model.DeliveryStatus
import com.kinectmessaging.libs.model.HistoryStatusCodes
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


const val DEFAULT_MAX_EVENTS = 10

@Service
class AzureDeliveryEventService {

    @Autowired
    lateinit var queueClient: QueueClient

    @Autowired
    lateinit var contactHistoryService: ContactHistoryService

    private val log = LoggerFactory.getLogger(this::class.java)

    @Value("\${spring.cloud.azure.storage.queue.max-events}")
    var maxEvents: Int = DEFAULT_MAX_EVENTS

    /**
     * This message receiver binding with [StorageQueueMessageSource]
     * via [MessageChannel] has name {@value INPUT_CHANNEL}
     */
    @ServiceActivator(inputChannel = INPUT_CHANNEL)
    fun messageReceiver(payload: ByteArray?, @Header(AzureHeaders.CHECKPOINTER) checkpointer: Checkpointer?) {
        val message = String(payload!!)
        log.info("Received Email Event Delivery message: {}", message)
        emailDeliveryEventProcessor(payload)
        checkpointer?.success()
            ?.doOnError { t -> log.error("Unable to checkpoint message. Error: {}", t.message)}
            ?.doOnSuccess { log.info("Message '{}' successfully checkpointed", message) }
            ?.block()
    }


    @OptIn(ExperimentalEncodingApi::class)
    fun emailDeliveryEventProcessor(queueMessage: ByteArray?){
        queueMessage?.let { message ->
            val messageBody = String(Base64.decode(message))
            log.info("${LogConstants.SERVICE_START} {}", kv("request", messageBody))
            val emailEvent : AzureEmailDeliveryReport = jacksonObjectMapper().readValue(messageBody)
            emailEvent.data.let { deliveryData ->
                val status =
                    when(deliveryData.status){
                        AzureEmailDeliveryStatus.Delivered, AzureEmailDeliveryStatus.Expanded -> {
                            HistoryStatusCodes.DELIVERED
                        }
                        else -> {
                            HistoryStatusCodes.FAILED
                        }
                    }
                contactHistoryService.updateContactMessageByDeliveryTrackingId(
                    deliveryTrackingId = deliveryData.messageId,
                    deliveryStatus = DeliveryStatus(
                        statusTime = deliveryData.deliveryAttemptTimestamp?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_ZONED_DATE_TIME) } ?: LocalDateTime.now(),
                        status = status,
                        statusMessage = deliveryData.deliveryStatusDetails.statusMessage,
                        originalStatus = null
                    ),
                    engagementStatus = null
                )
                log.info("${LogConstants.SERVICE_END} {} {}", kv("response", "Updated status $status for id ${deliveryData.messageId}"), kv("originalStatus", deliveryData.status))
            }
        }
    }

    companion object {
        private const val INPUT_CHANNEL = "input"
    }
}