package com.kinectmessaging.ch.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.kinectmessaging.libs.model.ContactMessages
import com.kinectmessaging.libs.model.KContactHistory
import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.PanacheMongoCompanion
import io.quarkus.mongodb.panache.kotlin.PanacheMongoEntityBase
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@MongoEntity(collection = "contact-history")
data class ContactHistoryEntity(
    @SerialName("_id")
    @field:JsonProperty("id")
    val id: String,
    @field:JsonProperty("sourceEventId")
    val sourceEventId: String,
    @field:JsonProperty("journeyTransactionId")
    val journeyTransactionId: String,
    @field:JsonProperty("journeyName")
    val journeyName: String,
    @field:JsonProperty("messages")
    val messages: ContactMessages,
): PanacheMongoEntityBase(){
    companion object: PanacheMongoCompanion<ContactHistoryEntity> {
        fun findById(id: String) = ContactHistoryEntity.find("_id", id).firstResult()
        fun findByMessageId(messageId: String) = ContactHistoryEntity.find("messages.messageId", messageId).firstResult()
        fun findByDeliveryTrackingId(deliveryTrackingId: String) = ContactHistoryEntity.find("messages.deliveryTrackingId", deliveryTrackingId).firstResult()
    }
    fun toResponse() = KContactHistory(
        id = id,
        sourceEventId = sourceEventId,
        journeyTransactionId = journeyTransactionId,
        journeyName = journeyName,
        messages = messages
    )
}

fun KContactHistory.toEntity() = ContactHistoryEntity(
    id = id,
    sourceEventId = sourceEventId,
    journeyTransactionId = journeyTransactionId,
    journeyName = journeyName,
    messages = messages
)


