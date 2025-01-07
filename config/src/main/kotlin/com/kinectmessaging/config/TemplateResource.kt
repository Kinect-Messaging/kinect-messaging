package com.kinectmessaging.config

import com.kinectmessaging.config.service.TemplateService
import com.kinectmessaging.libs.common.Defaults
import com.kinectmessaging.libs.common.LogConstants
import com.kinectmessaging.libs.model.KTemplate
import com.kinectmessaging.libs.model.TemplatePersonalizationRequest
import io.quarkus.logging.Log
import io.quarkus.panache.common.Sort
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.jboss.logging.MDC

private const val DEFAULT_SORT = "templateName"

@Path("/kinect/messaging/config/template")
class TemplateResource(private val templateService: TemplateService) {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun createTemplate(
        templateConfig: KTemplate,
    ): KTemplate {
        MDC.put("function", object {}.javaClass.enclosingMethod.name)
        Log.info("${LogConstants.SERVICE_START} with request - $templateConfig")
        val result = templateService.saveTemplate(templateConfig)
        Log.info("${LogConstants.SERVICE_END} with response - $result")
        return result
    }

    @POST
    @Path("/personalize")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun personalizeTemplate(
        templateRequest: TemplatePersonalizationRequest,
    ): List<KTemplate>? {
        MDC.put("function", object {}.javaClass.enclosingMethod.name)
        Log.info("${LogConstants.SERVICE_START} with request - $templateRequest")
        val result = templateService.personalizeTemplate(templateRequest)
        Log.info("${LogConstants.SERVICE_END} with response - $result")
        return result
    }

    @GET
    @Path("/{templateId}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getTemplateById(
        @PathParam("templateId") templateId: String,
    ): KTemplate? {
        MDC.put("function", object {}.javaClass.enclosingMethod.name)
        Log.info("${LogConstants.SERVICE_START} with request - $templateId")
        val result = templateService.findTemplateById(templateId)
        Log.info("${LogConstants.SERVICE_END} with response - $result")
        return result
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun getTemplates(
        @QueryParam(value = "page") page: Int? = Defaults.PAGE_NO,
        @QueryParam(value = "size") size: Int? = Defaults.PAGE_SIZE,
        @QueryParam(value = "sort") sort: String? = DEFAULT_SORT,
        @QueryParam(value = "order") order: Sort.Direction? = Sort.Direction.Ascending,
    ): List<KTemplate>? {

        MDC.put("function", object {}.javaClass.enclosingMethod.name)
        val pageNo = page ?: Defaults.PAGE_NO
        val pageSize = size ?: Defaults.PAGE_SIZE
        val sortBy = sort ?: DEFAULT_SORT
        val sortOrder = order ?: Sort.Direction.Ascending
        Log.info("${LogConstants.SERVICE_START}, page-number : $page, page-size : $size, sort-by : $sort")
        val result = templateService.findAllTemplates(pageNo, pageSize, sortBy, sortOrder)

        Log.info("${LogConstants.SERVICE_END} with response - $result")
        return result
    }
}