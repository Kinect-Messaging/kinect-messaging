package com.kinectmessaging.ep

import com.kinectmessaging.ep.service.EventProcessorService
import com.kinectmessaging.libs.common.LogConstants
import io.cloudevents.CloudEvent
import io.cloudevents.core.format.ContentType
import io.cloudevents.core.provider.EventFormatProvider
import io.quarkus.logging.Log
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.MediaType
import org.jboss.logging.MDC

@Path("/kinect/messaging/event")
class EventProcessorResource(private val eventProcessorService: EventProcessorService) {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    fun processEvent(event: String): String {
        MDC.put("function", object {}.javaClass.enclosingMethod.name)
        Log.info("${LogConstants.SERVICE_START} with request - $event")

        val cloudEvent: CloudEvent? = EventFormatProvider
            .getInstance()
            .resolveFormat(ContentType.JSON)
            ?.deserialize(event.encodeToByteArray())

        val result = cloudEvent?.let { eventProcessorService.processEvent(it) } ?: {
            Log.error("${LogConstants.SERVICE_END} with error. Invalid data received. Null or empty event.")
            throw BadRequestException("Invalid data received. Null or empty event")
        }
        Log.info("${LogConstants.SERVICE_END} with response - $result")
        return result as String
    }
}