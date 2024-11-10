package com.kinectmessaging.ch.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.kinectmessaging.ch.model.DeliveryData
import com.kinectmessaging.ch.service.AzureDeliveryEventService
import com.kinectmessaging.libs.common.Defaults
import com.kinectmessaging.libs.common.LogConstants
import com.kinectmessaging.libs.logging.MDCHelper
import com.kinectmessaging.libs.model.CloudEventsSchema
import com.kinectmessaging.libs.model.KContactHistory
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
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@RestController()
@RequestMapping("/kinect/messaging/azure-email-events")
class AzureEmailEventsController {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val objectMapper: ObjectMapper = ObjectMapper().registerKotlinModule().registerModule(JavaTimeModule())
    @Autowired
    lateinit var azureDeliveryEventService: AzureDeliveryEventService

    @OptIn(ExperimentalEncodingApi::class)
    @PostMapping(value = ["/delivery"])
    fun consumeEmailDeliveryEvents(
        @RequestBody event: ByteArray
    ) {
        val decodedEvent = String(Base64.decode(event))
        log.info("${LogConstants.SERVICE_START} {}", kv("request", decodedEvent))
        val cloudEvent: CloudEventsSchema = jacksonObjectMapper().readValue(decodedEvent)
        val headerMap = mutableMapOf(Pair(
            Defaults.TRANSACTION_ID_HEADER, cloudEvent.id))
        headerMap["contact-history-id"] = cloudEvent.id
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        MDCHelper.addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", kv("request", event))
//        val eventData = mapData(event, PojoCloudEventDataMapper.from(objectMapper, DeliveryData::class.java))
        val eventData = cloudEvent.data
        val contactHistory = objectMapper.convertValue<DeliveryData>(eventData)
        val result = contactHistory.let { azureDeliveryEventService.emailDeliveryEventProcessor(contactHistory) }
        log.info(
            "${LogConstants.SERVICE_END} {}",
            kv("response", result)
        )
    }
}