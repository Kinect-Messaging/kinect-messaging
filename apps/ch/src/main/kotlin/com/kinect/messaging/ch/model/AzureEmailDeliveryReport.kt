package com.kinect.messaging.ch.model

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

data class DeliveryData (
    val sender: String,
    val recipient: String,
    val messageId: String,
    val status: String,
    val deliveryStatusDetails: DeliveryStatusDetails,
    val deliveryAttemptTimeStamp: String
)

data class DeliveryStatusDetails (
    val statusMessage: String
)
