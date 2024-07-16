package com.kinectmessaging.config.controller

import com.kinectmessaging.config.service.JourneyService
import com.kinectmessaging.libs.common.Defaults
import com.kinectmessaging.libs.common.ErrorConstants
import com.kinectmessaging.libs.common.LogConstants
import com.kinectmessaging.libs.exception.InvalidInputException
import com.kinectmessaging.libs.logging.MDCHelper
import com.kinectmessaging.libs.logging.MDCHelper.addMDC
import com.kinectmessaging.libs.model.JourneyConfig
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private const val DEFAULT_SORT = "journeyName"

@RestController()
@RequestMapping("/kinect/messaging/config/journey")
class JourneyController {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var journeyService: JourneyService

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createJourney(
        @RequestBody journeyConfig: JourneyConfig,
        @RequestHeader(name = Defaults.TRANSACTION_ID_HEADER) transactionId: String
    ): ResponseEntity<JourneyConfig> {
        val headerMap = mutableMapOf(Pair("transaction-id", transactionId))
        headerMap["journey-id"] = journeyConfig.journeyId
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", kv("request", journeyConfig))
        val result = journeyService.saveJourney(journeyConfig)
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping("/{journeyId}")
    fun getJourneyById(
        @PathVariable journeyId: String,
        @RequestHeader(name = "X-Transaction-Id") transactionId: String
    ): ResponseEntity<JourneyConfig?> {
        val headerMap = mutableMapOf(Pair("transaction-id", transactionId))
        headerMap["journey-id"] = journeyId
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", kv("request", journeyId))
        val result = journeyService.findJourneyById(journeyId)
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getJourneys(
        @RequestParam(name = "_start", required = false) pageNo: Int? = Defaults.PAGE_NO,
        @RequestParam(name = "_end", required = false) pageSize: Int? = Defaults.PAGE_SIZE,
        @RequestParam(name = "_sort", required = false) sortBy: String? = DEFAULT_SORT,
        @RequestParam(name = "_order", required = false) sortOrder: Sort.Direction = Sort.Direction.ASC,
        @RequestHeader(name = Defaults.TRANSACTION_ID_HEADER) transactionId: String
    ): ResponseEntity<List<JourneyConfig>?> {
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
            journeyService.findJourneys(pageNo, pageSize, sortBy, sortOrder)
        } else {
            MDCHelper.clearMDC()
            throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, page-number : $pageNo, page-size : $pageSize, sort-by : $sortBy")
        }
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return ResponseEntity(result, HttpStatus.OK)
    }

}