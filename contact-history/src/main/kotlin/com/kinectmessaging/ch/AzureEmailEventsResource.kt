package com.kinectmessaging.ch

import com.fasterxml.jackson.databind.ObjectMapper
import com.kinectmessaging.ch.model.DeliveryData
import com.kinectmessaging.ch.service.AzureDeliveryEventService
import com.kinectmessaging.libs.common.LogConstants
import io.cloudevents.CloudEvent
import io.cloudevents.core.format.ContentType
import io.cloudevents.core.provider.EventFormatProvider
import io.cloudevents.jackson.JsonFormat
import io.cloudevents.jackson.PojoCloudEventDataMapper
import io.quarkus.logging.Log
import jakarta.inject.Inject
import jakarta.ws.rs.BadRequestException
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.MediaType
import org.jboss.logging.MDC

@Path("/kinect/messaging/azure-email-events")
class AzureEmailEventsResource(private val azureDeliveryEventService: AzureDeliveryEventService) {

    @Inject
    var mapper: ObjectMapper? = null

    @POST
    @Path("/delivery")
    @Consumes(MediaType.APPLICATION_JSON)
    fun consumeEmailDeliveryEvents(event: String){
        MDC.put("function", object {}.javaClass.enclosingMethod.name)
        Log.info("${LogConstants.SERVICE_START} with request - $event")
        val cloudEvent: CloudEvent? = EventFormatProvider
            .getInstance()
            .resolveFormat(ContentType.JSON)
            ?.deserialize(event.encodeToByteArray())

        cloudEvent?.data?.let { eventData ->
            val contactMessage = PojoCloudEventDataMapper.from(mapper, DeliveryData::class.java)
                .map(eventData).value
            val result = contactMessage.let { azureDeliveryEventService.emailDeliveryEventProcessor(it) }
            Log.info("${LogConstants.SERVICE_END} with response - $result")
        } ?: {
            Log.error("${LogConstants.SERVICE_END} with error. Invalid data received. Null or empty event.")
            throw BadRequestException("Invalid data received. Null or empty event")
        }
    }
}