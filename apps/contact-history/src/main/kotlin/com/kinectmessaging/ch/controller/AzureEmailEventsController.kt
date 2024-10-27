package com.kinectmessaging.ch.controller

import com.kinectmessaging.ch.model.AzureEmailDeliveryReport
import com.kinectmessaging.ch.service.ContactHistoryService
import com.kinectmessaging.libs.common.Defaults
import com.kinectmessaging.libs.common.LogConstants
import com.kinectmessaging.libs.logging.MDCHelper
import net.logstash.logback.argument.StructuredArguments
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/kinect/messaging/azure-email-events")
class AzureEmailEventsController {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var contactHistoryService: ContactHistoryService

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun consumeEmailEvents(
        @RequestBody azureEmailDeliveryReport: AzureEmailDeliveryReport
    ) {
        val headerMap = mutableMapOf(Pair(Defaults.TRANSACTION_ID_HEADER, azureEmailDeliveryReport.id))
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        MDCHelper.addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", StructuredArguments.kv("request", azureEmailDeliveryReport))
//        val result = contactHistoryService.processAzureEmailEvents(azureEmailDeliveryReport)
//        log.info(LogConstants.SERVICE_END, StructuredArguments.kv("response", result))
        MDCHelper.clearMDC()
    }

}