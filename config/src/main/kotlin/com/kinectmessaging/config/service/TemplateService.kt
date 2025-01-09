package com.kinectmessaging.config.service

import com.kinectmessaging.config.client.MjmlClient
import com.kinectmessaging.config.model.MjmlRequest
import com.kinectmessaging.config.model.TemplateEntity
import com.kinectmessaging.config.model.toEntity
import com.kinectmessaging.libs.common.ErrorConstants
import com.kinectmessaging.libs.exception.InvalidInputException
import com.kinectmessaging.libs.model.KTemplate
import com.kinectmessaging.libs.model.TemplatePersonalizationRequest
import io.quarkus.logging.Log
import io.quarkus.panache.common.Sort
import io.quarkus.qute.Qute
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.inject.RestClient
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


@ApplicationScoped
class TemplateService(
    @ConfigProperty(name = "app.client.mjml.url")
    val mjmlClientBaseUrl: String,
    ) {

    @Inject
    @field:RestClient
    lateinit var mjmlClient: MjmlClient

    fun findTemplateById(id: String): KTemplate? {
        val templateFromDB = TemplateEntity.findById(id)
        val result: KTemplate = templateFromDB?.toResponse()
            ?: throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}id - $id ")
        return result
    }

    fun saveTemplate(kTemplate: KTemplate): KTemplate {
        val templateFromDB = kTemplate.toEntity().persistOrUpdate()
        return kTemplate
    }

    fun findAllTemplates(pageNo: Int, pageSize: Int, sortBy: String, sortOrder: Sort.Direction): List<KTemplate>? {
        val templateQuery = TemplateEntity.findAll(Sort.by(sortBy, sortOrder)).page(pageNo, pageSize)
        val numberOfPages = templateQuery.pageCount()
        val count = templateQuery.count()
        if (count.toInt() == 0){
            throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, page-number - $pageNo, page-size - $pageSize, sort-by - $sortBy")
        }
        val dbResult = templateQuery.list()
        val result = mutableListOf<KTemplate>()
        dbResult.forEach {
            result.add(it.toResponse())
        }
        return result
    }

    fun personalizeTemplate(templatePersonalizationRequest: TemplatePersonalizationRequest): List<KTemplate> {
        val result = mutableListOf<KTemplate>()
        templatePersonalizationRequest.textTemplateId?.let { id ->
            getTemplateAndApplyPersonalization(
                id,
                templatePersonalizationRequest.personalizationData
            )?.let {
                val textTemplate = if (it.templateContent.contains("<mjml>")){
                    renderMjmlTemplate(it.templateContent)
                } else {
                    it.templateContent
                }
                result.add(it.copy(templateContent = textTemplate))
            }
        }
        templatePersonalizationRequest.htmlTemplateId?.let { id ->
            getTemplateAndApplyPersonalization(id, templatePersonalizationRequest.personalizationData)?.let {
                val htmlTemplate = renderMjmlTemplate(it.templateContent)
                Log.debug("Template after applying Mjml - $htmlTemplate" )
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
            Log.debug("Template before applying Mustache - $decodedContent" )
            val contentType = if (decodedContent.contains("<mjml>")) MediaType.TEXT_HTML else MediaType.TEXT_PLAIN
            val template = renderQuteTemplate(decodedContent, contextData, contentType)
            Log.debug("Template after applying Mustache - $template" )
            return templateFromDb.copy(templateContent = template)
        }
        return null
    }

    private fun renderQuteTemplate(template: String, context: Map<String, Map<String, String?>?>?, contentType: String?): String {
        Log.debug("Template context data: $context")
        val result = Qute.fmt(template).contentType(contentType).dataMap(context).render()
        return result
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun renderMjmlTemplate(template: String): String {
        val response = mjmlClient.renderMjmlToHtml(
            mjmlClientBaseUrl,
            MjmlRequest(Base64.encode(template.encodeToByteArray()))
        )
        val htmlContent = String(Base64.decode(response.html))
        return htmlContent
    }
}