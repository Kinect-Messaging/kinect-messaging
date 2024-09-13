package com.kinectmessaging.config.controller

import com.kinectmessaging.config.service.TemplateService
import com.kinectmessaging.libs.common.Defaults
import com.kinectmessaging.libs.common.ErrorConstants
import com.kinectmessaging.libs.common.LogConstants
import com.kinectmessaging.libs.exception.InvalidInputException
import com.kinectmessaging.libs.logging.MDCHelper
import com.kinectmessaging.libs.logging.MDCHelper.addMDC
import com.kinectmessaging.libs.model.KTemplate
import com.kinectmessaging.libs.model.TemplatePersonalizationRequest
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.io.encoding.ExperimentalEncodingApi

@RestController
@RequestMapping("/kinect/messaging/config/template")
class TemplateController {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var templateService: TemplateService

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createTemplate(
        @RequestBody kTemplate: KTemplate,
        @RequestHeader(name = "X-Transaction-Id") transactionId: String
    ): ResponseEntity<KTemplate?> {
        val headerMap = mutableMapOf(Pair("transaction-id", transactionId))
        headerMap["template-id"] = kTemplate.templateId
        addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", kv("request", kTemplate))
        val result = templateService.saveTemplate(kTemplate)
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping("/{templateId}")
    fun getTemplate(
        @PathVariable templateId: String,
        @RequestHeader(name = "X-Transaction-Id") transactionId: String
    ): ResponseEntity<KTemplate?> {
        val headerMap = mutableMapOf(Pair("transaction-id", transactionId))
        headerMap["template-id"] = templateId
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", kv("request", templateId))
        val result = templateService.findTemplateById(templateId)
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping()
    fun getAllTemplates(
        @RequestParam(name = "_start", required = false) pageNo: Int? = Defaults.PAGE_NO,
        @RequestParam(name = "_end", required = false) pageSize: Int? = Defaults.PAGE_SIZE,
        @RequestParam(name = "_sort", required = false) sortBy: String? = "templateName",
        @RequestParam(name = "_order", required = false) sortOrder: Sort.Direction = Sort.Direction.ASC,
        @RequestHeader(name = "X-Transaction-Id") transactionId: String
    ): ResponseEntity<List<KTemplate>?> {
        val headerMap = mutableMapOf(Pair("transaction-id", transactionId))
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        addMDC(headerMap)
        log.info(
            "${LogConstants.SERVICE_START} {} {} {} {}",
            kv("page-number", pageNo),
            kv("page-size", pageSize),
            kv("sort-by", sortBy),
            kv("sort-order", sortOrder)
        )
        val result = if (pageNo != null && pageSize != null && sortBy?.isNotBlank() == true) {
            templateService.findTemplates(pageNo, pageSize, sortBy, sortOrder)
        } else {
            MDCHelper.clearMDC()
            log.error("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, page-number : $pageNo, page-size : $pageSize, sort-by : $sortBy")
            throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, page-number : $pageNo, page-size : $pageSize, sort-by : $sortBy")
        }
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return ResponseEntity(result, HttpStatus.OK)
    }

    @ExperimentalEncodingApi
    @PostMapping("/personalize")
    suspend fun personalizeTemplate(
        @RequestBody templateRequest: TemplatePersonalizationRequest,
        @RequestHeader(name = "X-Transaction-Id") transactionId: String
    ): ResponseEntity<List<KTemplate>?> {
        val headerMap = mutableMapOf(Pair("transaction-id", transactionId))
        headerMap["html-template-id"] = templateRequest.htmlTemplateId.toString()
        headerMap["text-template-id"] = templateRequest.textTemplateId.toString()
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", kv("request", templateRequest))
        val result = templateService.personalizeTemplate(templateRequest)
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return ResponseEntity(result, HttpStatus.OK)
    }
}