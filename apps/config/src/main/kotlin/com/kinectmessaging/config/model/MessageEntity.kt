package com.kinectmessaging.config.model

import com.kinectmessaging.libs.model.Audit
import com.kinectmessaging.libs.model.EmailConfig
import com.kinectmessaging.libs.model.MessageStatus
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "message-config")
data class MessageEntity(
    @Id
    val messageId: String,
    @Indexed
    val messageName: String,
    val messageVersion: Int = 1,
    val messageCondition: String?,
    val messageStatus: MessageStatus? = MessageStatus.DRAFT,
    val emailConfig: List<EmailConfig>?,
    val auditInfo: Audit,
    val journeyId: String?
)
