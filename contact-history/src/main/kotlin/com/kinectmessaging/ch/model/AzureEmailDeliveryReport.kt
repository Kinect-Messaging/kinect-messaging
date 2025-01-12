package com.kinectmessaging.ch.model

import kotlinx.serialization.Serializable

data class AzureEmailDeliveryReport(
    val id: String,
    val topic: String,
    val subject: String,
    val data: DeliveryData,
    val eventType: String,
    val dataVersion: String,
    val metadataVersion: String,
    val eventTime: String
)

@Serializable
data class DeliveryData (
    val sender: String,
    val recipient: String,
    val messageId: String,
    val status: AzureEmailDeliveryStatus,
    val deliveryStatusDetails: DeliveryStatusDetails,
    val deliveryAttemptTimestamp: String?
)

@Serializable
data class DeliveryStatusDetails (
    val statusMessage: String
)

@Serializable
enum class AzureEmailDeliveryStatus {
    Delivered,
    Suppressed,
    Bounced,
    Quarantined,
    FilteredSpam,
    Expanded,
    Failed
}
