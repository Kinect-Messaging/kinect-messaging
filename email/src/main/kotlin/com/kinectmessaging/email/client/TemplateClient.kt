package com.kinectmessaging.email.com.kinectmessaging.email.client

import com.kinectmessaging.libs.model.KTemplate
import com.kinectmessaging.libs.model.TemplatePersonalizationRequest
import io.quarkus.rest.client.reactive.NotBody
import io.quarkus.rest.client.reactive.Url
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@RegisterRestClient(configKey = "template-api")
interface TemplateClient {
    @POST
//    @Path("/kinect/messaging/config/template")
    @Consumes(MediaType.APPLICATION_JSON)
    fun loadTemplate(
        @Url url: String?,
        personalizationRequest: TemplatePersonalizationRequest
    ): List<KTemplate>?
}