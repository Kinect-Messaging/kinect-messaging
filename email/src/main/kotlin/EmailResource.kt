package com.kinectmessaging.email

import com.fasterxml.jackson.databind.ObjectMapper
import com.kinectmessaging.email.com.kinectmessaging.email.client.CloudEventSerializer
import com.kinectmessaging.email.service.EmailService
import com.kinectmessaging.libs.common.LogConstants
import com.kinectmessaging.libs.model.KMessage
import com.kinectmessaging.libs.model.TargetSystem
import io.cloudevents.CloudEvent
import io.cloudevents.jackson.JsonFormat
import io.cloudevents.jackson.PojoCloudEventDataMapper
import io.quarkus.logging.Log
import jakarta.inject.Inject
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.MediaType
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jboss.logging.MDC

@Path("/kinect/messaging/email")
class EmailResource(private val emailService: EmailService) {

    @Inject
    private lateinit var mapper: ObjectMapper

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    fun sendEmail(kMessage: KMessage){
        MDC.put("function", object {}.javaClass.enclosingMethod.name)
        Log.info("${LogConstants.SERVICE_START} with request - ${Json.encodeToString(kMessage)}")
        val result = callEmailService(kMessage)
        Log.info("${LogConstants.SERVICE_END} with response - $result")
    }

    @Path("/message")
    @Consumes(MediaType.APPLICATION_JSON, JsonFormat.CONTENT_TYPE)
    fun sendEmailFromQueue(event: @Serializable(with = CloudEventSerializer::class) CloudEvent){
        MDC.put("function", object {}.javaClass.enclosingMethod.name)
        Log.info("${LogConstants.SERVICE_START} with request - $event")
        event.data?.let { eventData ->
            val message = PojoCloudEventDataMapper.from(mapper, KMessage::class.java)
                .map(eventData).value
            val result = callEmailService(message)
            Log.info("${LogConstants.SERVICE_END} with response - $result")
        } ?: {
            Log.error("${LogConstants.SERVICE_END} with error. Invalid data received. Null or empty event.")
            throw BadRequestException("Invalid data received. Null or empty event")
        }
    }

    private fun callEmailService(kMessage: KMessage): String{
        var result = "No email sent."
        when(kMessage.targetSystem){
            TargetSystem.AZURE_COMMUNICATION_SERVICE -> {
                result = emailService.deliverEmail(kMessage) ?: "No email sent."
            }
            TargetSystem.AWS_SIMPLE_EMAIL_SERVICE -> TODO()
        }
        return result
    }
}