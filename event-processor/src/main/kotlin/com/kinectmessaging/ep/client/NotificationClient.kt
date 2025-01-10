package com.kinectmessaging.ep.client

import io.cloudevents.jackson.JsonFormat
import io.quarkus.rest.client.reactive.NotBody
import io.quarkus.rest.client.reactive.Url
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@RegisterRestClient(configKey = "notification-api")
interface NotificationClient {
    @POST
    @Consumes(JsonFormat.CONTENT_TYPE)
    @ClientHeaderParam(name = "aeg-sas-key", value = ["\${app.client.notification.access-key}"])
    fun sendNotification(
        @NotBody @Url url: String?,
        event: ByteArray
    )
}