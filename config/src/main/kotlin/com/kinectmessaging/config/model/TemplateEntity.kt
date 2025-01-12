package com.kinectmessaging.config.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.kinectmessaging.libs.model.Audit
import com.kinectmessaging.libs.model.KTemplate
import com.kinectmessaging.libs.model.Language
import com.kinectmessaging.libs.model.TemplateType
import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.PanacheMongoCompanion
import io.quarkus.mongodb.panache.kotlin.PanacheMongoEntityBase
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@MongoEntity(collection = "template-config")
data class TemplateEntity(
    @SerialName("_id")
    @field:JsonProperty("templateId")
    val templateId: String,
    @field:JsonProperty("templateName")
    val templateName: String,
    @field:JsonProperty("templateType")
    val templateType: TemplateType = TemplateType.CONTROL,
    @field:JsonProperty("templateLanguage")
    val templateLanguage: Language = Language.EN,
    @field:JsonProperty("templateContent")
    val templateContent: String,
    @field:JsonProperty("auditInfo")
    val auditInfo: Audit
): PanacheMongoEntityBase(){
    companion object: PanacheMongoCompanion<TemplateEntity> {
        fun findById(id: String) = TemplateEntity.find("_id", id).firstResult()
    }

    fun toResponse() = KTemplate(
        templateId = templateId,
        templateName = templateName,
        templateType = templateType,
        templateLanguage = templateLanguage,
        templateContent = templateContent,
        auditInfo = auditInfo
    )
}

fun KTemplate.toEntity() = TemplateEntity(
    templateId = templateId,
    templateName = templateName,
    templateType = templateType,
    templateLanguage = templateLanguage,
    templateContent = templateContent,
    auditInfo = auditInfo
)
