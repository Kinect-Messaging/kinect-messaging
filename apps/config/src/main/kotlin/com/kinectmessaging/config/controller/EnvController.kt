package com.kinectmessaging.config.controller

import com.kinectmessaging.config.service.EnvService
import com.kinectmessaging.libs.common.Defaults
import com.kinectmessaging.libs.common.ErrorConstants
import com.kinectmessaging.libs.common.LogConstants
import com.kinectmessaging.libs.exception.InvalidInputException
import com.kinectmessaging.libs.logging.MDCHelper
import com.kinectmessaging.libs.model.EnvConfig
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private const val DEFAULT_SORT = "envName"

@RestController
@RequestMapping("/kinect/messaging/config/env")
class EnvController {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var envService: EnvService

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun saveEnv(
        @RequestBody envConfig: EnvConfig,
        @RequestHeader(name = "X-Transaction-Id") transactionId: String
    ): ResponseEntity<EnvConfig> {
        val headerMap = mutableMapOf(Pair("transaction-id", transactionId))
        headerMap["env-id"] = envConfig.envId
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        MDCHelper.addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", kv("request", envConfig))
        val result = envService.saveEnv(envConfig)
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping("/{envId}")
    fun getEnvById(
        @PathVariable envId: String,
        @RequestHeader(name = "X-Transaction-Id") transactionId: String
    ): ResponseEntity<EnvConfig?> {
        val headerMap = mutableMapOf(Pair("transaction-id", transactionId))
        headerMap["env-id"] = envId
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        MDCHelper.addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", kv("request", envId))
        val result = envService.findEnvById(envId)
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getEnvs(
        @RequestParam(name = "_start", required = false) pageNo: Int? = Defaults.PAGE_NO,
        @RequestParam(name = "_end", required = false) pageSize: Int? = Defaults.PAGE_SIZE,
        @RequestParam(name = "_sort", required = false) sortBy: String? = DEFAULT_SORT,
        @RequestParam(name = "_order", required = false) sortOrder: Sort.Direction = Sort.Direction.ASC,
        @RequestHeader(name = "X-Transaction-Id") transactionId: String
    ): ResponseEntity<List<EnvConfig>?> {
        val headerMap = mutableMapOf(Pair("transaction-id", transactionId))
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        MDCHelper.addMDC(headerMap)
        log.info(
            "${LogConstants.SERVICE_START} {} {} {} {}",
            kv("page-number", pageNo),
            kv("page-size", pageSize),
            kv("sort-by", sortBy),
            kv("sort-order", sortOrder)
        )
        val result = if (pageNo != null && pageSize != null && sortBy?.isNotBlank() == true) {
            envService.findEnvs(pageNo, pageSize, sortBy, sortOrder)
        } else {
            MDCHelper.clearMDC()
            throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, page-number : $pageNo, page-size : $pageSize, sort-by : $sortBy")
        }
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return ResponseEntity(result, HttpStatus.OK)
    }
}