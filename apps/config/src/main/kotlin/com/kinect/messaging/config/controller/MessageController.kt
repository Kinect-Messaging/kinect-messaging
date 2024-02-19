package com.kinect.messaging.config.controller

import com.kinect.messaging.config.service.MessageService
import com.kinect.messaging.libs.common.Defaults
import com.kinect.messaging.libs.common.ErrorConstants
import com.kinect.messaging.libs.common.LogConstants
import com.kinect.messaging.libs.exception.InvalidInputException
import com.kinect.messaging.libs.logging.MDCHelper
import com.kinect.messaging.libs.logging.MDCHelper.addMDC
import com.kinect.messaging.libs.model.MessageConfig
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping("/kinect/messaging/config/message")
class MessageController {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var messageService: MessageService

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createMessage(
        @RequestBody messageConfig: MessageConfig,
        @RequestHeader headers: Map<String, String?>
    ): MessageConfig {
        val headerMap = headers.filter { it.key.startsWith("X-") }.toMutableMap()
        headerMap["message-id"] = messageConfig.messageId
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", kv("request", messageConfig))
        val result = messageService.saveMessage(messageConfig)
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return result
    }

    @GetMapping("/{messageId}")
    fun getMessageById(@PathVariable messageId: String, @RequestHeader headers: Map<String, String?>): MessageConfig? {
        val headerMap = headers.filter { it.key.startsWith("X-") }.toMutableMap()
        headerMap["message-id"] = messageId
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", kv("request", messageId))
        val result = messageService.findMessageById(messageId)
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return result
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getMessages(
        @RequestParam pageNo: Int? = Defaults.PAGE_NO,
        @RequestParam pageSize: Int? = Defaults.PAGE_SIZE,
        @RequestParam sortBy: String? = "messageName",
        @RequestHeader headers: Map<String, String?>
    ): List<MessageConfig>? {
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
            messageService.findMessages(pageNo, pageSize, sortBy)
        } else {
            MDCHelper.clearMDC()
            throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, page-number : $pageNo, page-size : $pageSize, sort-by : $sortBy")
        }
        log.info(LogConstants.SERVICE_END, kv("response", result))
        MDCHelper.clearMDC()
        return result
    }

}