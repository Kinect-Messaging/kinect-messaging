package com.kinectmessaging.config.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.kinectmessaging.libs.model.Audit
import com.kinectmessaging.libs.model.EmailConfig
import com.kinectmessaging.libs.model.MessageConfig
import com.kinectmessaging.libs.model.MessageStatus
import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.PanacheMongoCompanion
import io.quarkus.mongodb.panache.kotlin.PanacheMongoEntityBase
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@MongoEntity(collection = "message-config")
data class MessageEntity(
    @SerialName("_id")
    @field:JsonProperty("messageId")
    val messageId: String,
    @field:JsonProperty("messageName")
    val messageName: String,
    @field:JsonProperty("messageVersion")
    val messageVersion: Int = 1,
    @field:JsonProperty("messageCondition")
    val messageCondition: String?,
    @field:JsonProperty("messageStatus")
    val messageStatus: MessageStatus? = MessageStatus.DRAFT,
    @field:JsonProperty("emailConfig")
    val emailConfig: List<EmailConfig>?,
    @field:JsonProperty("auditInfo")
    val auditInfo: Audit,
    @field:JsonProperty("journeyId")
    val journeyId: String?
) : PanacheMongoEntityBase(){

    companion object: PanacheMongoCompanion<MessageEntity> {
        fun findById(id: String) = MessageEntity.find("_id", id).firstResult()
    }

    fun toResponse() = MessageConfig(
        messageId = messageId,
        messageName = messageName,
        messageCondition = messageCondition,
        emailConfig = emailConfig,
        messageVersion = messageVersion,
        messageStatus = messageStatus,
        journeyId = journeyId,
        auditInfo = auditInfo
    )
}

fun MessageConfig.toEntity() = MessageEntity(
    messageId = messageId,
    messageName = messageName,
    messageCondition = messageCondition,
    emailConfig = emailConfig,
    messageVersion = messageVersion,
    messageStatus = messageStatus,
    journeyId = journeyId,
    auditInfo = auditInfo
)
