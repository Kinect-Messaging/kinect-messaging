package com.kinect.messaging.config.service

import com.kinect.messaging.config.client.MjmlClient
import com.kinect.messaging.config.model.MjmlRequest
import com.kinect.messaging.config.model.TemplateEntity
import com.kinect.messaging.config.repository.TemplateRepository
import com.kinect.messaging.libs.common.ErrorConstants
import com.kinect.messaging.libs.exception.InvalidInputException
import com.kinect.messaging.libs.model.KTemplate
import com.kinect.messaging.libs.model.TemplatePersonalizationRequest
import com.samskivert.mustache.Mustache
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.jvm.optionals.getOrNull


@Service
class TemplateService {

    @Autowired
    lateinit var mjmlClient: MjmlClient

    @Autowired
    lateinit var templateRepository: TemplateRepository

    fun findTemplateById(id: String): KTemplate? {
        val templateFromDB = templateRepository.findById(id)
        val result: KTemplate = templateFromDB.getOrNull()?.toTemplate()
            ?: throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}id - $id ")
        return result
    }

    fun saveTemplate(kTemplate: KTemplate): KTemplate? {
        val templateFromDB = templateRepository.save(kTemplate.toTemplateEntity())
        return templateFromDB.toTemplate()
    }

    fun findTemplates(pageNo: Int, pageSize: Int, sortBy: String = "templateName", sortOrder: Sort.Direction): List<KTemplate>? {
        val pageOption = PageRequest.of(pageNo, pageSize, sortOrder, sortBy)
        val pageResult = templateRepository.findAll(pageOption)
        val dbResult = pageResult.content
        val result = mutableListOf<KTemplate>()
        if (pageResult.isEmpty) {
            throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE} page-number - $pageNo, page-size - $pageSize, sort-by - $sortBy")
        }
        /*while (pageResult.hasNext()) {
            val nextPageable = pageResult.nextPageable()
            val page = templateRepository.findAll(nextPageable)
            dbResult = page.content
        }*/
        dbResult.forEach {
            result.add(it.toTemplate())
        }
        return result
    }

    suspend fun personalizeTemplate(templatePersonalizationRequest: TemplatePersonalizationRequest): List<KTemplate> {
        val result = mutableListOf<KTemplate>()
        templatePersonalizationRequest.textTemplateId?.let { id ->
            getTemplateAndApplyPersonalization(
                id,
                templatePersonalizationRequest.personalizationData
            )?.let { result.add(it) }
        }
        templatePersonalizationRequest.htmlTemplateId?.let { id ->
            getTemplateAndApplyPersonalization(id, templatePersonalizationRequest.personalizationData)?.let {
                val htmlTemplate = renderMjmlTemplate(it.templateContent)
                result.add(it.copy(templateContent = htmlTemplate))
            }
        }
        return result
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun getTemplateAndApplyPersonalization(
        id: String,
        contextData: Map<String, Map<String, String?>?>?
    ): KTemplate? {
        val templateFromDb = findTemplateById(id)
        templateFromDb?.let {
            val decodedContent = String(Base64.decode(it.templateContent))
            val template = renderMustacheTemplate(decodedContent, contextData)
            return templateFromDb.copy(templateContent = template)
        }
        return null
    }

    private fun renderMustacheTemplate(template: String, context: Map<String, Map<String, String?>?>?): String {
        val compiler = Mustache.compiler()
        val result = compiler.compile(template).execute(context)
        return result
    }

    private suspend fun renderMjmlTemplate(template: String): String {
        val htmlContent = mjmlClient.renderMjmlToHtml(MjmlRequest(template))
        return htmlContent
    }

    fun KTemplate.toTemplateEntity() = TemplateEntity(
        templateId = templateId,
        templateName = templateName,
        templateType = templateType,
        templateLanguage = templateLanguage,
        templateContent = templateContent,
        auditInfo = auditInfo
    )

    fun TemplateEntity.toTemplate() = KTemplate(
        templateId = templateId,
        templateName = templateName,
        templateType = templateType,
        templateLanguage = templateLanguage,
        templateContent = templateContent,
        auditInfo = auditInfo
    )
}