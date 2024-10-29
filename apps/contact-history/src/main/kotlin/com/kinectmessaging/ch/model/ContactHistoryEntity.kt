package com.kinectmessaging.ch.model

import com.kinectmessaging.libs.model.ContactMessages
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "contact-history")
data class ContactHistoryEntity(
    @Id
    val id: String,
    val sourceEventId: String,
    val journeyTransactionId: String,
    @Indexed
    val journeyName: String,
    val messages: ContactMessages,
)


