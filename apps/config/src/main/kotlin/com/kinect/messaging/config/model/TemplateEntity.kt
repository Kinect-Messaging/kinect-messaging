package com.kinect.messaging.config.model

import com.kinect.messaging.libs.model.Audit
import com.kinect.messaging.libs.model.TemplateLanguage
import com.kinect.messaging.libs.model.TemplateType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "template-config")
data class TemplateEntity(
    @Id
    val templateId: String,
    @Indexed
    val templateName: String,
    val templateType: TemplateType = TemplateType.CONTROL,
    val templateLanguage: TemplateLanguage = TemplateLanguage.EN,
    val templateContent: String,
    val auditInfo: Audit
)
