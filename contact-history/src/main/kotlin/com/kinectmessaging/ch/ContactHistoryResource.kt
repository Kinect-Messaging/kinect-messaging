package com.kinectmessaging.ch

import com.fasterxml.jackson.databind.ObjectMapper
import com.kinectmessaging.ch.service.ContactHistoryService
import com.kinectmessaging.libs.common.Defaults
import com.kinectmessaging.libs.common.LogConstants
import com.kinectmessaging.libs.model.ContactMessages
import com.kinectmessaging.libs.model.KContactHistory
import io.cloudevents.CloudEvent
import io.cloudevents.core.format.ContentType
import io.cloudevents.core.provider.EventFormatProvider
import io.cloudevents.jackson.JsonFormat
import io.cloudevents.jackson.PojoCloudEventDataMapper
import io.quarkus.logging.Log
import io.quarkus.panache.common.Sort
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.jboss.logging.MDC


const val DEFAULT_SORT = "journeyName"

@Path("/kinect/messaging/contact-history")
class ContactHistoryResource(private val contactHistoryService: ContactHistoryService) {

    @Inject
    lateinit var mapper: ObjectMapper

    @POST
    @Consumes(MediaType.APPLICATION_JSON, JsonFormat.CONTENT_TYPE)
    fun createContactHistory(
        event: String,
    ) {
        MDC.put("function", object {}.javaClass.enclosingMethod.name)
        Log.info("${LogConstants.SERVICE_START} with request - $event")
        val cloudEvent: CloudEvent? = EventFormatProvider
            .getInstance()
            .resolveFormat(ContentType.JSON)
            ?.deserialize(event.encodeToByteArray())

        cloudEvent?.data?.let { eventData ->
            val contactHistory =
                PojoCloudEventDataMapper.from(mapper, KContactHistory::class.java)
                .map(eventData).value
            val result = contactHistory.let { contactHistoryService.saveContactHistory(it) }
            Log.info("${LogConstants.SERVICE_END} with response - $result")
        } ?: {
            Log.error("${LogConstants.SERVICE_END} with error. Invalid data received. Null or empty event.")
            throw BadRequestException("Invalid data received. Null or empty event")
        }
    }

    @GET
    @Path("/{contactHistoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getContactHistoryById(
        @PathParam("contactHistoryId") contactHistoryId: String,
    ): KContactHistory? {
        MDC.put("function", object {}.javaClass.enclosingMethod.name)
        Log.info("${LogConstants.SERVICE_START} with request - $contactHistoryId")
        val result = contactHistoryService.findContactHistoryById(contactHistoryId)
        Log.info("${LogConstants.SERVICE_END} with response - $result")
        return result
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun getAllContactHistory(
        @QueryParam(value = "page") page: Int? = Defaults.PAGE_NO,
        @QueryParam(value = "size") size: Int? = Defaults.PAGE_SIZE,
        @QueryParam(value = "sort") sort: String? = DEFAULT_SORT,
        @QueryParam(value = "order") order: Sort.Direction? = Sort.Direction.Ascending,
    ): List<KContactHistory>? {

        MDC.put("function", object {}.javaClass.enclosingMethod.name)
        val pageNo = page ?: Defaults.PAGE_NO
        val pageSize = size ?: Defaults.PAGE_SIZE
        val sortBy = sort ?: DEFAULT_SORT
        val sortOrder = order ?: Sort.Direction.Ascending
        Log.info("${LogConstants.SERVICE_START}, page-number : $page, page-size : $size, sort-by : $sort")
        val result = contactHistoryService.findAllContactHistory(pageNo, pageSize, sortBy, sortOrder)

        Log.info("${LogConstants.SERVICE_END} with response - $result")
        return result
    }

    @POST
    @Path("/message")
    @Consumes(MediaType.APPLICATION_JSON, JsonFormat.CONTENT_TYPE)
    fun updateContactMessageByMessageId(event: String){
        MDC.put("function", object {}.javaClass.enclosingMethod.name)
        Log.info("${LogConstants.SERVICE_START} with request - $event")
        val cloudEvent: CloudEvent? = EventFormatProvider
            .getInstance()
            .resolveFormat(ContentType.JSON)
            ?.deserialize(event.encodeToByteArray())

        cloudEvent?.data?.let { eventData ->
            val contactMessage = PojoCloudEventDataMapper.from(mapper, ContactMessages::class.java)
                .map(eventData).value
            val result = contactMessage.let { contactHistoryService.updateContactMessageByMessageId(it) }
            Log.info("${LogConstants.SERVICE_END} with response - $result")
        } ?: {
            Log.error("${LogConstants.SERVICE_END} with error. Invalid data received. Null or empty event.")
            throw BadRequestException("Invalid data received. Null or empty event")
        }
    }
}