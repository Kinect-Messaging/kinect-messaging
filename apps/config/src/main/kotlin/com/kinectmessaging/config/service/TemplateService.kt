package com.kinectmessaging.config.service

import com.kinectmessaging.config.client.MjmlClient
import com.kinectmessaging.config.model.MjmlRequest
import com.kinectmessaging.config.model.TemplateEntity
import com.kinectmessaging.config.repository.TemplateRepository
import com.kinectmessaging.libs.common.ErrorConstants
import com.kinectmessaging.libs.exception.InvalidInputException
import com.kinectmessaging.libs.model.KTemplate
import com.kinectmessaging.libs.model.TemplatePersonalizationRequest
import com.samskivert.mustache.Mustache
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.jvm.optionals.getOrNull


@Service
class TemplateService {

    private val log = LoggerFactory.getLogger(this::class.java)

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
                log.debug("Template after applying Mjml - $htmlTemplate" )
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
            log.debug("Template before applying Mustache - $decodedContent" )
            val template = renderMustacheTemplate(decodedContent, contextData)
            log.debug("Template after applying Mustache - $decodedContent" )
            return templateFromDb.copy(templateContent = template)
        }
        return null
    }

    private fun renderMustacheTemplate(template: String, context: Map<String, Map<String, String?>?>?): String {
        val compiler = Mustache.compiler()
        val result = compiler.compile(template).execute(context)
        return result
    }

    @OptIn(ExperimentalEncodingApi::class)
    private suspend fun renderMjmlTemplate(template: String): String {
        val response = mjmlClient.renderMjmlToHtml(MjmlRequest(Base64.encode(template.encodeToByteArray())))
        val htmlContent = String(Base64.decode(response.html))
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