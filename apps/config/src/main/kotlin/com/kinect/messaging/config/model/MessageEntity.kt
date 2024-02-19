package com.kinect.messaging.config.model

import com.azure.spring.data.cosmos.core.mapping.Container
import com.azure.spring.data.cosmos.core.mapping.PartitionKey
import com.kinect.messaging.libs.model.Audit
import com.kinect.messaging.libs.model.EmailConfig
import com.kinect.messaging.libs.model.MessageStatus
import org.springframework.data.annotation.Id

@Container(containerName = "message-config")
data class MessageEntity(
    @Id
    val messageId: String,
    @PartitionKey
    val messageName: String,
    val messageVersion: Int = 1,
    val messageCondition: String?,
    val messageStatus: MessageStatus? = MessageStatus.DRAFT,
    val emailConfig: List<EmailConfig>?,
    val auditInfo: Audit,
    val journeyId: String?
)
