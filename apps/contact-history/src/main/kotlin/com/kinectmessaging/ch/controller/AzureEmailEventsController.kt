package com.kinectmessaging.ch.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.kinectmessaging.ch.model.DeliveryData
import com.kinectmessaging.ch.service.AzureDeliveryEventService
import com.kinectmessaging.libs.common.LogConstants
import io.cloudevents.CloudEvent
import io.cloudevents.core.CloudEventUtils.mapData
import io.cloudevents.jackson.PojoCloudEventDataMapper
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/kinect/messaging/azure-email-events")
class AzureEmailEventsController {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val objectMapper: ObjectMapper = ObjectMapper().registerKotlinModule().registerModule(JavaTimeModule())
    @Autowired
    lateinit var azureDeliveryEventService: AzureDeliveryEventService

    @PostMapping(value = ["/delivery"])
    fun consumeEmailDeliveryEvents(
        @RequestBody event: CloudEvent
    ) {
        log.info("${LogConstants.SERVICE_START} {}", kv("request", event))
        val eventData = mapData(event, PojoCloudEventDataMapper.from(objectMapper, DeliveryData::class.java))
        val contactHistory = eventData?.value
        val result = contactHistory?.let { azureDeliveryEventService.emailDeliveryEventProcessor(it) }
        log.info(
            "${LogConstants.SERVICE_END} {}",
            kv("response", result)
        )
    }
}