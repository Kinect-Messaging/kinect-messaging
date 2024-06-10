package com.kinect.messaging.ch.model

import com.kinect.messaging.libs.model.ContactMessages
import com.kinect.messaging.libs.model.DeliveryChannel
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "contact-history")
data class ContactHistoryEntity(
    @Id
    val id: String,
    val journeyTransactionId: String,
    val journeyName: String,
    val messages: ContactMessages,
)


