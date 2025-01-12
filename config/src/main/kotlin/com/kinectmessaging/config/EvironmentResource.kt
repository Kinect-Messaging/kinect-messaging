package com.kinectmessaging.config

import com.kinectmessaging.config.service.EnvironmentService
import com.kinectmessaging.libs.common.Defaults
import com.kinectmessaging.libs.common.LogConstants
import com.kinectmessaging.libs.model.EnvConfig
import io.quarkus.logging.Log
import io.quarkus.panache.common.Sort
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import org.jboss.logging.MDC

private const val DEFAULT_SORT = "envName"

@Path("/kinect/messaging/config/env")
class EvironmentResource(private val environmentService: EnvironmentService) {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun createEnvironment(
        envConfig: EnvConfig,
    ): EnvConfig {
        MDC.put("function", object {}.javaClass.enclosingMethod.name)
        Log.info("${LogConstants.SERVICE_START} with request - $envConfig")
        val result = environmentService.saveEnvironment(envConfig)
        Log.info("${LogConstants.SERVICE_END} with response - $result")
        return result
    }

    @POST
    @Path("/publish")
    @Consumes(MediaType.APPLICATION_JSON)
    fun publishEnvironments(envConfigs: List<EnvConfig>,){
        MDC.put("function", object {}.javaClass.enclosingMethod.name)
        Log.info("${LogConstants.SERVICE_START} with request - $envConfigs")
        val result = environmentService.publishEnvironments(envConfigs)
        Log.info("${LogConstants.SERVICE_END} with response - $result")
    }

    @GET
    @Path("/{envId}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getEnvironmentById(
        @PathParam("envId") environmentId: String,
    ): EnvConfig? {
        MDC.put("function", object {}.javaClass.enclosingMethod.name)
        Log.info("${LogConstants.SERVICE_START} with request - $environmentId")
        val result = environmentService.findEnvironmentById(environmentId)
        Log.info("${LogConstants.SERVICE_END} with response - $result")
        return result
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun getEnvironments(
        @QueryParam(value = "page") page: Int? = Defaults.PAGE_NO,
        @QueryParam(value = "size") size: Int? = Defaults.PAGE_SIZE,
        @QueryParam(value = "sort") sort: String? = DEFAULT_SORT,
        @QueryParam(value = "order") order: Sort.Direction? = Sort.Direction.Ascending,
    ): List<EnvConfig>? {

        MDC.put("function", object {}.javaClass.enclosingMethod.name)
        val pageNo = page ?: Defaults.PAGE_NO
        val pageSize = size ?: Defaults.PAGE_SIZE
        val sortBy = sort ?: DEFAULT_SORT
        val sortOrder = order ?: Sort.Direction.Ascending
        Log.info("${LogConstants.SERVICE_START}, page-number : $page, page-size : $size, sort-by : $sort")
        val result = environmentService.findAllEnvironments(pageNo, pageSize, sortBy, sortOrder)
        Log.info("${LogConstants.SERVICE_END} with response - $result")
        return result
    }
}