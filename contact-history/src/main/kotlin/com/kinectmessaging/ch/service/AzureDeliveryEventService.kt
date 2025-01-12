package com.kinectmessaging.ch.service

import com.kinectmessaging.ch.model.AzureEmailDeliveryStatus
import com.kinectmessaging.ch.model.DeliveryData
import com.kinectmessaging.libs.model.DeliveryStatus
import com.kinectmessaging.libs.model.HistoryStatusCodes
import jakarta.enterprise.context.ApplicationScoped
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@ApplicationScoped
class AzureDeliveryEventService(private val contactHistoryService: ContactHistoryService) {

    fun emailDeliveryEventProcessor(deliveryData: DeliveryData): String {
        val status =
            when (deliveryData.status) {
                AzureEmailDeliveryStatus.Delivered, AzureEmailDeliveryStatus.Expanded -> {
                    HistoryStatusCodes.DELIVERED
                }

                else -> {
                    HistoryStatusCodes.FAILED
                }
            }
        deliveryData.internetMessageId?.let { messageId ->
            contactHistoryService.updateContactMessageByDeliveryTrackingId(
                deliveryTrackingId = messageId,
                deliveryStatus = DeliveryStatus(
                    statusTime = deliveryData.deliveryAttemptTimestamp?.let {
                        LocalDateTime.parse(
                            it,
                            DateTimeFormatter.ISO_ZONED_DATE_TIME
                        )
                    } ?: LocalDateTime.now(),
                    status = status,
                    statusMessage = deliveryData.deliveryStatusDetails?.statusMessage,
                    originalStatus = null
                ),
                engagementStatus = null
            )
        }
        return "Updated status $status for id ${deliveryData.messageId}"
    }
}