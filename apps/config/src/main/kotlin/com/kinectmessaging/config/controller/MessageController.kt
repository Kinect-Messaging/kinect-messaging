package com.kinectmessaging.config.controller

import com.kinectmessaging.config.service.MessageService
import com.kinectmessaging.libs.common.Defaults
import com.kinectmessaging.libs.common.ErrorConstants
import com.kinectmessaging.libs.common.LogConstants
import com.kinectmessaging.libs.exception.InvalidInputException
import com.kinectmessaging.libs.logging.MDCHelper
import com.kinectmessaging.libs.logging.MDCHelper.addMDC
import com.kinectmessaging.libs.model.MessageConfig
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

private const val DEFAULT_SORT = "messageName"

@RestController()
@RequestMapping("/kinect/messaging/config/message")
class MessageController {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var messageService: MessageService

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createMessage(
        @RequestBody messageConfig: MessageConfig,
        @RequestHeader(name = "X-Transaction-Id") transactionId: String
    ): ResponseEntity<MessageConfig> {
        val headerMap = mutableMapOf(Pair("transaction-id", transactionId))
        headerMap["message-id"] = messageConfig.messageId
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", kv("request", messageConfig))
        val result = messageService.saveMessage(messageConfig)
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping("/{messageId}")
    fun getMessageById(
        @PathVariable messageId: String,
        @RequestHeader(name = "X-Transaction-Id") transactionId: String
    ): ResponseEntity<MessageConfig?> {
        val headerMap = mutableMapOf(Pair("transaction-id", transactionId))
        headerMap["message-id"] = messageId
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", kv("request", messageId))
        val result = messageService.findMessageById(messageId)
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getMessages(
        @RequestParam(name = "_start", required = false) pageNo: Int? = Defaults.PAGE_NO,
        @RequestParam(name = "_end", required = false) pageSize: Int? = Defaults.PAGE_SIZE,
        @RequestParam(name = "_sort", required = false) sortBy: String? = DEFAULT_SORT,
        @RequestParam(name = "_order", required = false) sortOrder: Sort.Direction = Sort.Direction.ASC,
        @RequestHeader(name = "X-Transaction-Id") transactionId: String
    ): ResponseEntity<List<MessageConfig>?> {
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
            messageService.findMessages(pageNo, pageSize, sortBy, sortOrder)
        } else {
            MDCHelper.clearMDC()
            log.error("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, page-number : $pageNo, page-size : $pageSize, sort-by : $sortBy")
            throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, page-number : $pageNo, page-size : $pageSize, sort-by : $sortBy")
        }
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return ResponseEntity(result, HttpStatus.OK)
    }

}