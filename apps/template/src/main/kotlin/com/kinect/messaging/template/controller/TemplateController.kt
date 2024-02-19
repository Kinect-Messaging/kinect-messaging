package com.kinect.messaging.template.controller

import com.kinect.messaging.libs.common.ErrorConstants
import com.kinect.messaging.libs.common.LogConstants
import com.kinect.messaging.libs.exception.InvalidInputException
import com.kinect.messaging.libs.logging.MDCHelper
import com.kinect.messaging.libs.logging.MDCHelper.addMDC
import com.kinect.messaging.libs.model.KTemplate
import com.kinect.messaging.libs.model.TemplatePersonalizationRequest
import com.kinect.messaging.template.service.TemplateService
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import kotlin.io.encoding.ExperimentalEncodingApi

@RestController
@RequestMapping("/kinect/messaging/template")
class TemplateController {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var templateService: TemplateService

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createTemplate(@RequestBody kTemplate: KTemplate, @RequestHeader headers: Map<String, String?>): KTemplate? {
        val headerMap = headers.filter { it.key.startsWith("X-") }.toMutableMap()
        headerMap["template-id"] = kTemplate.templateId
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", kv("request", kTemplate))
        val result = templateService.saveTemplate(kTemplate)
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return result
    }

    @GetMapping("/{templateId}")
    fun getTemplate(@PathVariable templateId: String, @RequestHeader headers: Map<String, String?>): KTemplate? {
        val headerMap = headers.filter { it.key.startsWith("X-") }.toMutableMap()
        headerMap["template-id"] = templateId
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", kv("request", templateId))
        val result = templateService.findTemplateById(templateId)
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return result
    }

    @GetMapping()
    fun getAllTemplates(
        @RequestParam pageNo: Int? = 1,
        @RequestParam pageSize: Int? = 20,
        @RequestParam sortBy: String? = "journeyName", @RequestHeader headers: Map<String, String?>
    ): List<KTemplate>? {
        val headerMap = headers.filter { it.key.startsWith("X-") }.toMutableMap()
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        addMDC(headerMap)
        log.info(
            "${LogConstants.SERVICE_START} {} {} {}",
            kv("page-number", pageNo),
            kv("page-size", pageSize),
            kv("sort-by", sortBy)
        )
        val result = if (pageNo != null && pageSize != null && sortBy?.isNotBlank() == true) {
            templateService.findTemplates(pageNo, pageSize, sortBy)
        } else {
            MDCHelper.clearMDC()
            throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, page-number : $pageNo, page-size : $pageSize, sort-by : $sortBy")
        }
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return result
    }

    @ExperimentalEncodingApi
    @PostMapping("/personalize")
    suspend fun personalizeTemplate(
        @RequestBody templateRequest: TemplatePersonalizationRequest,
        @RequestHeader headers: Map<String, String?>
    ): List<KTemplate> {
        val headerMap = headers.filter { it.key.startsWith("X-") }.toMutableMap()
        headerMap["html-template-id"] = templateRequest.htmlTemplateId.toString()
        headerMap["text-template-id"] = templateRequest.textTemplateId.toString()
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", kv("request", templateRequest))
        val result = templateService.personalizeTemplate(templateRequest)
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return result
    }
}