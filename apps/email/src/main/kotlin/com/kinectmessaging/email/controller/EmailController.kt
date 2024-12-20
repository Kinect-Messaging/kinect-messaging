package com.kinectmessaging.email.controller

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.kinectmessaging.email.service.EmailService
import com.kinectmessaging.libs.common.Defaults
import com.kinectmessaging.libs.common.LogConstants
import com.kinectmessaging.libs.logging.MDCHelper
import com.kinectmessaging.libs.model.CloudEventsSchema
import com.kinectmessaging.libs.model.KMessage
import com.kinectmessaging.libs.model.TargetSystem
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import net.logstash.logback.argument.StructuredArguments
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping("/kinect/messaging/email")
class EmailController {

    private val objectMapper: ObjectMapper = ObjectMapper().registerKotlinModule().registerModule(JavaTimeModule()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var azureEmailService: EmailService

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun sendEmail(@RequestBody kMessage: KMessage, @RequestHeader(Defaults.TRANSACTION_ID_HEADER) transactionId: String): ResponseEntity<String>{
        var result = "No email sent."
            val headerMap = mutableMapOf(Pair("transaction-id", transactionId))
        headerMap["message-id"] = kMessage.id
        headerMap["event-id"] = kMessage.sourceId
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        MDCHelper.addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", StructuredArguments.kv("request", kMessage))
        result = callEmailService(kMessage)
        log.info(LogConstants.SERVICE_END, StructuredArguments.kv("response", result))
        MDCHelper.clearMDC()
        return ResponseEntity(result, HttpStatus.OK)
    }

    @PostMapping("/queue")
    suspend fun sendEmailFromQueue(@RequestBody event: CloudEventsSchema){
        var result = "No email sent."
        val headerMap = mutableMapOf(Pair(Defaults.TRANSACTION_ID_HEADER, event.id))
        headerMap["message-id"] = event.id
        headerMap["method"] = object {}.javaClass.enclosingMethod.name
        MDCHelper.addMDC(headerMap)
        log.info("${LogConstants.SERVICE_START} {}", StructuredArguments.kv("request", event))
        val eventData = event.data
        val kMessage = objectMapper.convertValue<KMessage>(eventData)
        result = callEmailService(kMessage)
        log.info(LogConstants.SERVICE_END, StructuredArguments.kv("response", result))
        MDCHelper.clearMDC()
    }

    private suspend fun callEmailService(kMessage: KMessage): String{
        var result = "No email sent."
        when(kMessage.targetSystem){
            TargetSystem.AZURE_COMMUNICATION_SERVICE ->
            {
                result = withContext(MDCContext()){
                    azureEmailService.deliverEmail(kMessage) ?: "No email sent."
                }
            }
            TargetSystem.AWS_SIMPLE_EMAIL_SERVICE -> TODO()
        }
        return result
    }
}

