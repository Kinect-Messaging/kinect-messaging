package com.kinectmessaging.config.client

import com.kinectmessaging.config.model.MjmlRequest
import com.kinectmessaging.config.model.MjmlResponse
import io.quarkus.rest.client.reactive.NotBody
import io.quarkus.rest.client.reactive.Url
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@RegisterRestClient(configKey = "mjml-api")
interface MjmlClient {
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    fun renderMjmlToHtml(
        @Url url: String?,
        mjmlRequest: MjmlRequest,
    ): MjmlResponse
}
