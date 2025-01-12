package com.kinectmessaging.ch.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
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

@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class DeliveryData (
    val sender: String?,
    val recipient: String?,
    val internetMessageId: String?,
    val messageId: String?,
    val status: AzureEmailDeliveryStatus?,
    val deliveryStatusDetails: DeliveryStatusDetails?,
    val deliveryAttemptTimestamp: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
@Serializable
data class DeliveryStatusDetails (
    val statusMessage: String?,
    val recipientMailServerHostName: String?
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
