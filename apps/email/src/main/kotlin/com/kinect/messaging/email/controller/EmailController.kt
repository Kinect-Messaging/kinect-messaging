package com.kinect.messaging.email.controller

import com.kinect.messaging.email.service.EmailService
import com.kinect.messaging.libs.model.KMessage
import com.kinect.messaging.libs.model.TargetSystem
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController()
@RequestMapping("/kinect/messaging/email")
class EmailController {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var azureEmailService: EmailService

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun sendEmail(@RequestBody kMessage: KMessage): String{
        var result = "No email sent."
        when(kMessage.targetSystem){
            TargetSystem.AZURE_COMMUNICATION_SERVICE ->
            {
                result = azureEmailService.deliverEmail(kMessage) ?: "No email sent."
            }

            TargetSystem.AWS_SIMPLE_EMAIL_SERVICE -> TODO()
        }
        log.info("Result from Email Controller - $result")
        return result
    }
}