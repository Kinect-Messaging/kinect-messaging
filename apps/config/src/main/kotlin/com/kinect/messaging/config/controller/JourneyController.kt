package com.kinect.messaging.config.controller

import com.kinect.messaging.config.service.JourneyService
import com.kinect.messaging.libs.common.Defaults
import com.kinect.messaging.libs.common.ErrorConstants
import com.kinect.messaging.libs.common.LogConstants
import com.kinect.messaging.libs.exception.InvalidInputException
import com.kinect.messaging.libs.logging.MDCHelper
import com.kinect.messaging.libs.logging.MDCHelper.addMDC
import com.kinect.messaging.libs.model.JourneyConfig
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping("/kinect/messaging/config/journey")
class JourneyController {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var journeyService: JourneyService

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createJourney(
        @RequestBody journeyConfig: JourneyConfig,
        @RequestHeader headers: Map<String, String?>
    ): JourneyConfig {
        val headerMap = headers.filter { it.key.startsWith("X-") }.toMutableMap()
        headerMap["journey-id"] = journeyConfig.journeyId
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", kv("request", journeyConfig))
        val result = journeyService.saveJourney(journeyConfig)
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return result
    }

    @GetMapping("/{journeyId}")
    fun getJourneyById(@PathVariable journeyId: String, @RequestHeader headers: Map<String, String?>): JourneyConfig? {
        val headerMap = headers.filter { it.key.startsWith("X-") }.toMutableMap()
        headerMap["journey-id"] = journeyId
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", kv("request", journeyId))
        val result = journeyService.findJourneyById(journeyId)
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return result
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getJourneys(
        @RequestParam pageNo: Int? = Defaults.PAGE_NO,
        @RequestParam pageSize: Int? = Defaults.PAGE_SIZE,
        @RequestParam sortBy: String? = "journeyName",
        @RequestHeader headers: Map<String, String?>
    ): List<JourneyConfig>? {
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
            journeyService.findJourneys(pageNo, pageSize, sortBy)
        } else {
            MDCHelper.clearMDC()
            throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, page-number : $pageNo, page-size : $pageSize, sort-by : $sortBy")
        }
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return result
    }

}