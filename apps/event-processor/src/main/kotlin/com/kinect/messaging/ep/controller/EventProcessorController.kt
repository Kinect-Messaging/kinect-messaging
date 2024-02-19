package com.kinect.messaging.ep.controller

import com.kinect.messaging.ep.service.EventProcessorService
import com.kinect.messaging.libs.common.DateUtils
import com.kinect.messaging.libs.common.LogConstants
import com.kinect.messaging.libs.logging.MDCHelper.addMDC
import com.kinect.messaging.libs.model.KEvent
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/kinect/messaging/event")
class EventProcessorController {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var eventProcessorService: EventProcessorService

    @PostMapping
    fun processEvent(@RequestBody event: KEvent, @RequestHeader headers: Map<String, String?>): String{
        val headerMap = headers.filter { it.key.startsWith("X-") }.toMutableMap()
        headerMap["event-id"] = event.eventId
        headerMap["event-name"] = event.eventName
        headerMap["event-time"] = DateUtils.toIsoLocalDateTimeFormat(event.eventTime)
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", kv("request", event))
        val result = eventProcessorService.processEvent(event)
        log.info(LogConstants.SERVICE_END, kv("response", result))
        return result
    }
}