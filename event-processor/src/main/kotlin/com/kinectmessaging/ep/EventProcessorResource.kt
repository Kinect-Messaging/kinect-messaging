package com.kinectmessaging.ep

import com.kinectmessaging.ep.service.EventProcessorService
import com.kinectmessaging.libs.common.LogConstants
import com.kinectmessaging.libs.model.KEvent
import io.quarkus.logging.Log
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.MediaType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jboss.logging.MDC

@Path("/kinect/messaging/event")
class EventProcessorResource(private val eventProcessorService: EventProcessorService) {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    fun processEvent(event: KEvent): String {
        MDC.put("function", object {}.javaClass.enclosingMethod.name)
        MDC.put("event-id", event.eventId)
        MDC.put("event-name", event.eventName)
        Log.info("${LogConstants.SERVICE_START} with request - $event")
        val result = eventProcessorService.processEvent(event)
        Log.info("${LogConstants.SERVICE_END} with response - $result")
        return result
    }
}