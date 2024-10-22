package com.kinectmessaging.ch.controller

import com.kinectmessaging.ch.service.ContactHistoryService
import com.kinectmessaging.libs.common.Defaults
import com.kinectmessaging.libs.common.ErrorConstants
import com.kinectmessaging.libs.common.LogConstants
import com.kinectmessaging.libs.exception.InvalidInputException
import com.kinectmessaging.libs.logging.MDCHelper
import com.kinectmessaging.libs.model.KContactHistory
import net.logstash.logback.argument.StructuredArguments
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

const val DEFAULT_SORT = "journey-name"
@RestController()
@RequestMapping("/kinect/messaging/contact-history")
class ContactHistoryController {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var contactHistoryService: ContactHistoryService

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createContactHistory(
        @RequestBody contactHistory: KContactHistory,
        @RequestHeader(name = Defaults.TRANSACTION_ID_HEADER) transactionId: String
    ) {
        val headerMap = mutableMapOf(Pair("transaction-id", transactionId))
        headerMap["contact-history-id"] = contactHistory.id
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        MDCHelper.addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", StructuredArguments.kv("request", contactHistory))
        val result = contactHistoryService.saveContactHistory(contactHistory)
        log.info(LogConstants.SERVICE_END, StructuredArguments.kv("response", result))
        MDCHelper.clearMDC()
    }

    @GetMapping("/{contactHistoryId}")
    fun getContactHistoryById(
        @PathVariable contactHistoryId: String,
        @RequestHeader(name = Defaults.TRANSACTION_ID_HEADER) transactionId: String
    ): ResponseEntity<KContactHistory?> {
        val headerMap = mutableMapOf(Pair("transaction-id", transactionId))
        headerMap["contact-history-id"] = contactHistoryId
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        MDCHelper.addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", StructuredArguments.kv("request", contactHistoryId))
        val result = contactHistoryService.findContactHistoryById(contactHistoryId)
        log.info(LogConstants.SERVICE_END, StructuredArguments.kv("response", result))
        MDCHelper.clearMDC()
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getContactHistory(
        @RequestParam(name = "_start", required = false) pageNo: Int? = Defaults.PAGE_NO,
        @RequestParam(name = "_end", required = false) pageSize: Int? = Defaults.PAGE_SIZE,
        @RequestParam(name = "_sort", required = false) sortBy: String? = DEFAULT_SORT,
        @RequestParam(name = "_order", required = false) sortOrder: Sort.Direction = Sort.Direction.ASC,
        @RequestHeader(name = Defaults.TRANSACTION_ID_HEADER) transactionId: String
    ): ResponseEntity<List<KContactHistory>?> {
        val headerMap = mutableMapOf(Pair("transaction-id", transactionId))
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        MDCHelper.addMDC(headerMap)
        log.info(
            "${LogConstants.SERVICE_START} {} {} {} {}",
            StructuredArguments.kv("page-number", pageNo),
            StructuredArguments.kv("page-size", pageSize),
            StructuredArguments.kv("sort-by", sortBy),
            StructuredArguments.kv("sort-order", sortOrder)
        )
        val result = if (pageNo != null && pageSize != null && sortBy?.isNotBlank() == true) {
            contactHistoryService.findContactHistory(pageNo, pageSize, sortBy, sortOrder)
        } else {
            MDCHelper.clearMDC()
            throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, page-number : $pageNo, page-size : $pageSize, sort-by : $sortBy")
        }
        log.info(LogConstants.SERVICE_END, StructuredArguments.kv("response", result))
        MDCHelper.clearMDC()
        return ResponseEntity(result, HttpStatus.OK)
    }

}