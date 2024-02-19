package com.kinect.messaging.config.controller

import com.kinect.messaging.config.model.EnvEntity
import com.kinect.messaging.config.service.EnvService
import com.kinect.messaging.libs.common.Defaults
import com.kinect.messaging.libs.common.ErrorConstants
import com.kinect.messaging.libs.common.LogConstants
import com.kinect.messaging.libs.exception.InvalidInputException
import com.kinect.messaging.libs.logging.MDCHelper
import com.kinect.messaging.libs.model.EnvConfig
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/kinect/messaging/config/env")
class EnvController {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var envService: EnvService

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun saveEnv(@RequestBody envConfig: EnvConfig, @RequestHeader headers: Map<String, String?>): EnvConfig {
        val headerMap = headers.filter { it.key.startsWith("X-") }.toMutableMap()
        headerMap["env-id"] = envConfig.envId
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        MDCHelper.addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", kv("request", envConfig))
        val result = envService.saveEnv(envConfig)
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return result
    }

    @GetMapping("/{envId}")
    fun getEnvById(@PathVariable envId: String, @RequestHeader headers: Map<String, String?>): EnvConfig? {
        val headerMap = headers.filter { it.key.startsWith("X-") }.toMutableMap()
        headerMap["env-id"] = envId
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        MDCHelper.addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", kv("request", envId))
        val result = envService.findEnvById(envId)
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return result
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getEnvs(
        @RequestParam pageNo: Int? = Defaults.PAGE_NO,
        @RequestParam pageSize: Int? = Defaults.PAGE_SIZE,
        @RequestParam sortBy: String? = "envName",
        @RequestHeader headers: Map<String, String?>
    ): List<EnvConfig>? {
        val headerMap = headers.filter { it.key.startsWith("X-") }.toMutableMap()
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        MDCHelper.addMDC(headerMap)
        log.info(
            "${LogConstants.SERVICE_START} {} {} {}",
            kv("page-number", pageNo),
            kv("page-size", pageSize),
            kv("sort-by", sortBy)
        )
        val result = if (pageNo != null && pageSize != null && sortBy?.isNotBlank() == true) {
            envService.findEnvs(pageNo, pageSize, sortBy)
        } else {
            MDCHelper.clearMDC()
            throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, page-number : $pageNo, page-size : $pageSize, sort-by : $sortBy")
        }
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return result
    }
}