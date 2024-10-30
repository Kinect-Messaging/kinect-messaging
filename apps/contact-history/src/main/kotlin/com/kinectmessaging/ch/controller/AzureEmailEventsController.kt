package com.kinectmessaging.ch.controller

import com.kinectmessaging.ch.service.AzureDeliveryEventService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/kinect/messaging/azure-email-events")
class AzureEmailEventsController {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var azureDeliveryEventService: AzureDeliveryEventService

    @PostMapping(value = ["/delivery"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun consumeEmailDeliveryEvents(
        @RequestBody message: ByteArray?
    ) {
        azureDeliveryEventService.emailDeliveryEventProcessor(message)
    }
}