package com.kinect.messaging.template.model

import com.azure.spring.data.cosmos.core.mapping.Container
import com.azure.spring.data.cosmos.core.mapping.PartitionKey
import com.kinect.messaging.libs.model.TemplateLanguage
import com.kinect.messaging.libs.model.TemplateType
import org.springframework.data.annotation.Id

@Container(containerName = "templates")
data class TemplateEntity(
    @Id
    val templateId: String,
    @PartitionKey
    val templateName: String,
    val templateType: TemplateType = TemplateType.CONTROL,
    val templateLanguage: TemplateLanguage = TemplateLanguage.EN,
    val templateContent: String
)
