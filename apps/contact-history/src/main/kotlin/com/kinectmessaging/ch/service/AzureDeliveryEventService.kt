package com.kinectmessaging.ch.service

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
import org.springframework.stereotype.Service
import java.time.LocalDateTime

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

    fun emailDeliveryEventsListener(){
        queueClient.create()

        val queueMessages = queueClient.receiveMessages(maxEvents)
        queueMessages.forEach { message ->
            val messageBody = String(message.body.toBytes())
            log.info("${LogConstants.SERVICE_START} {}", kv("request", messageBody))
            val emailEvent : AzureEmailDeliveryReport = jacksonObjectMapper().readValue(messageBody)
            emailEvent.data.let {
                val status =
                    when(it.status){
                        AzureEmailDeliveryStatus.Delivered, AzureEmailDeliveryStatus.Expanded -> {
                            HistoryStatusCodes.DELIVERED
                        }
                        else -> {
                            HistoryStatusCodes.FAILED
                        }
                    }
                contactHistoryService.updateContactMessageByDeliveryTrackingId(
                    deliveryTrackingId = it.messageId,
                    deliveryStatus = DeliveryStatus(
                        statusTime = LocalDateTime.parse(it.deliveryAttemptTimeStamp),
                        status = status,
                        statusMessage = it.deliveryStatusDetails.statusMessage,
                        originalStatus = null
                    ),
                    engagementStatus = null
                )
                log.info(LogConstants.SERVICE_END, kv("response", "Updated status $status for id ${it.messageId}"), kv("originalStatus", it.status))
            }
        }
    }
}