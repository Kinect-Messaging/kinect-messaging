package com.kinectmessaging.config.model

import com.kinectmessaging.libs.model.Audit
import com.kinectmessaging.libs.model.TemplateLanguage
import com.kinectmessaging.libs.model.TemplateType
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
