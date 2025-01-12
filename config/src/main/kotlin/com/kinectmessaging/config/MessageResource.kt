package com.kinectmessaging.config

import com.kinectmessaging.config.service.MessageService
import com.kinectmessaging.libs.common.Defaults
import com.kinectmessaging.libs.common.LogConstants
import com.kinectmessaging.libs.model.MessageConfig
import io.quarkus.logging.Log
import io.quarkus.panache.common.Sort
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.jboss.logging.MDC

private const val DEFAULT_SORT = "messageName"

@Path("/kinect/messaging/config/message")
class MessageResource(private val messageService: MessageService) {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun createMessage(
        messageConfig: MessageConfig,
    ): MessageConfig {
        MDC.put("function", object {}.javaClass.enclosingMethod.name)
        Log.info("${LogConstants.SERVICE_START} with request - $messageConfig")
        val result = messageService.saveMessage(messageConfig)
        Log.info("${LogConstants.SERVICE_END} with response - $result")
        return result
    }

    @GET
    @Path("/{messageId}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getMessageById(
        @PathParam("messageId") messageId: String,
    ): MessageConfig? {
        MDC.put("function", object {}.javaClass.enclosingMethod.name)
        Log.info("${LogConstants.SERVICE_START} with request - $messageId")
        val result = messageService.findMessageById(messageId)
        Log.info("${LogConstants.SERVICE_END} with response - $result")
        return result
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun getMessages(
        @QueryParam(value = "page") page: Int? = Defaults.PAGE_NO,
        @QueryParam(value = "size") size: Int? = Defaults.PAGE_SIZE,
        @QueryParam(value = "sort") sort: String? = DEFAULT_SORT,
        @QueryParam(value = "order") order: Sort.Direction? = Sort.Direction.Ascending,
    ): List<MessageConfig>? {

        MDC.put("function", object {}.javaClass.enclosingMethod.name)
        val pageNo = page ?: Defaults.PAGE_NO
        val pageSize = size ?: Defaults.PAGE_SIZE
        val sortBy = sort ?: DEFAULT_SORT
        val sortOrder = order ?: Sort.Direction.Ascending
        Log.info("${LogConstants.SERVICE_START}, page-number : $page, page-size : $size, sort-by : $sort")
        val result = messageService.findAllMessages(pageNo, pageSize, sortBy, sortOrder)
        Log.info("${LogConstants.SERVICE_END} with response - $result")
        return result
    }
}