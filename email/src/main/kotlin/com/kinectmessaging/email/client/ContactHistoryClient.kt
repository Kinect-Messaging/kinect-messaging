package com.kinectmessaging.email.com.kinectmessaging.email.client

import io.cloudevents.CloudEvent
import io.cloudevents.jackson.JsonFormat
import io.quarkus.rest.client.reactive.NotBody
import io.quarkus.rest.client.reactive.Url
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.HeaderParam
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@RegisterRestClient(configKey = "contact-history-api")
interface ContactHistoryClient {
    @POST
    @Consumes(JsonFormat.CONTENT_TYPE)
    @ClientHeaderParam(name = "aeg-sas-key", value = ["\${app.client.contact-history.access-key}"])
    fun updateContactMessages(
        @Url url: String?,
        event: CloudEvent
    )
}