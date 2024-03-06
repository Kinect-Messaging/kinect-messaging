package com.kinect.messaging.email.controller

import com.kinect.messaging.email.service.EmailService
import com.kinect.messaging.libs.common.Defaults
import com.kinect.messaging.libs.common.LogConstants
import com.kinect.messaging.libs.logging.MDCHelper
import com.kinect.messaging.libs.model.KMessage
import com.kinect.messaging.libs.model.TargetSystem
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import net.logstash.logback.argument.StructuredArguments
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/kinect/messaging/email")
class EmailController {

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
        when(kMessage.targetSystem){
            TargetSystem.AZURE_COMMUNICATION_SERVICE ->
            {
                result = withContext(MDCContext()){
                    azureEmailService.deliverEmail(kMessage) ?: "No email sent."
                }

            }

            TargetSystem.AWS_SIMPLE_EMAIL_SERVICE -> TODO()
        }
        log.info(LogConstants.SERVICE_END, StructuredArguments.kv("response", result))
        MDCHelper.clearMDC()
        return ResponseEntity(result, HttpStatus.OK)
    }
}