package com.kinectmessaging.config

import com.kinectmessaging.config.service.JourneyService
import com.kinectmessaging.libs.common.Defaults
import com.kinectmessaging.libs.common.LogConstants
import com.kinectmessaging.libs.model.JourneyConfig
import io.quarkus.logging.Log
import io.quarkus.panache.common.Sort
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.jboss.logging.MDC

private const val DEFAULT_SORT = "journeyName"

@Path("/kinect/messaging/config/journey")
class JourneyResource(private val journeyService: JourneyService) {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun createJourney(
        journeyConfig: JourneyConfig,
    ): JourneyConfig {
        MDC.put("function", object {}.javaClass.enclosingMethod.name)
        Log.info("${LogConstants.SERVICE_START} with request - $journeyConfig")
        val result = journeyService.saveJourney(journeyConfig)
        Log.info("${LogConstants.SERVICE_END} with response - $result")
        return result
    }

    @GET
    @Path("/{journeyId}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getJourneyById(
        @PathParam("journeyId") journeyId: String,
    ): JourneyConfig? {
        MDC.put("function", object {}.javaClass.enclosingMethod.name)
        Log.info("${LogConstants.SERVICE_START} with request - $journeyId")
        val result = journeyService.findJourneyById(journeyId)
        Log.info("${LogConstants.SERVICE_END} with response - $result")
        return result
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun getJourneys(
        @QueryParam(value = "event-name") eventName: String?,
        @QueryParam(value = "page") page: Int? = Defaults.PAGE_NO,
        @QueryParam(value = "size") size: Int? = Defaults.PAGE_SIZE,
        @QueryParam(value = "sort") sort: String? = DEFAULT_SORT,
        @QueryParam(value = "order") order: Sort.Direction? = Sort.Direction.Ascending,
    ): List<JourneyConfig>? {

        MDC.put("function", object {}.javaClass.enclosingMethod.name)
        val pageNo = page ?: Defaults.PAGE_NO
        val pageSize = size ?: Defaults.PAGE_SIZE
        val sortBy = sort ?: DEFAULT_SORT
        val sortOrder = order ?: Sort.Direction.Ascending
        Log.info("${LogConstants.SERVICE_START}, event-name: $eventName , page-number : $page, page-size : $size, sort-by : $sort")
        val result = eventName?.let {
            journeyService.findJourneysByEventName(eventName)
        } ?: journeyService.findAllJourneys(pageNo, pageSize, sortBy, sortOrder)

        Log.info("${LogConstants.SERVICE_END} with response - $result")
        return result
    }
}