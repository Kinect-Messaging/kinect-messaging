package com.kinectmessaging.ch.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.kinectmessaging.ch.model.AzureEmailDeliveryReport
import com.kinectmessaging.ch.service.ContactHistoryService
import com.kinectmessaging.libs.common.Defaults
import com.kinectmessaging.libs.common.ErrorConstants
import com.kinectmessaging.libs.common.LogConstants
import com.kinectmessaging.libs.exception.InvalidInputException
import com.kinectmessaging.libs.logging.MDCHelper
import com.kinectmessaging.libs.model.CloudEventsSchema
import com.kinectmessaging.libs.model.ContactMessages
import com.kinectmessaging.libs.model.KContactHistory
import io.cloudevents.CloudEvent
import io.cloudevents.core.CloudEventUtils.mapData
import io.cloudevents.core.impl.BaseCloudEvent
import io.cloudevents.jackson.PojoCloudEventDataMapper
import net.logstash.logback.argument.StructuredArguments
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

const val DEFAULT_SORT = "journeyName"
@RestController()
@RequestMapping("/kinect/messaging/contact-history")
class ContactHistoryController {

    private val log = LoggerFactory.getLogger(this::class.java)

    private val objectMapper: ObjectMapper = ObjectMapper().registerKotlinModule().registerModule(JavaTimeModule())
    @Autowired
    lateinit var contactHistoryService: ContactHistoryService

    @OptIn(ExperimentalEncodingApi::class)
    @PostMapping()
    fun createContactHistory(
        @RequestBody event: ByteArray,
        @RequestHeader(name = Defaults.TRANSACTION_ID_HEADER, required = false) transactionId: String?
    ) {
        val decodedEvent = String(Base64.decode(event))
        log.info("${LogConstants.SERVICE_START} {}", kv("request", decodedEvent))
        val cloudEvent: CloudEventsSchema = jacksonObjectMapper().readValue(decodedEvent)
        val headerMap = transactionId?.let { mutableMapOf(Pair(Defaults.TRANSACTION_ID_HEADER, transactionId)) } ?: mutableMapOf(Pair(Defaults.TRANSACTION_ID_HEADER, cloudEvent.id))
        headerMap["contact-history-id"] = cloudEvent.id
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        MDCHelper.addMDC(headerMap)
//        val eventData = mapData(event, PojoCloudEventDataMapper.from(objectMapper, KContactHistory::class.java))
        val eventData = cloudEvent.data
        val contactHistory = objectMapper.convertValue<KContactHistory>(eventData)
        val result = contactHistory.let { contactHistoryService.saveContactHistory(it) }
            ?: throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE} for event $event")
        log.info(LogConstants.SERVICE_END, StructuredArguments.kv("response", result))
        MDCHelper.clearMDC()
    }

    @OptIn(ExperimentalEncodingApi::class)
    @PostMapping(value = ["/message"])
    fun updateContactMessageByMessageId(
        @RequestBody event: ByteArray,
        @RequestHeader(name = Defaults.TRANSACTION_ID_HEADER, required = false) transactionId: String?
    ) {
        val decodedEvent = String(Base64.decode(event))
        log.info("${LogConstants.SERVICE_START} {}", kv("request", decodedEvent))
        val cloudEvent: CloudEventsSchema = jacksonObjectMapper().readValue(decodedEvent)
        val headerMap = transactionId?.let { mutableMapOf(Pair(Defaults.TRANSACTION_ID_HEADER, transactionId)) } ?: mutableMapOf(Pair(Defaults.TRANSACTION_ID_HEADER, cloudEvent.id))
        headerMap["contact-history-id"] = cloudEvent.id
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        MDCHelper.addMDC(headerMap)
//        val eventData = mapData(event, PojoCloudEventDataMapper.from(objectMapper, ContactMessages::class.java))
        val eventData = cloudEvent.data
        val contactMessage = objectMapper.convertValue<ContactMessages>(eventData)
        val result = contactMessage.let { contactHistoryService.updateContactMessageByMessageId(it) }
            ?: throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE} for event $event")
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