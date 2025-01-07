package com.kinectmessaging.ep.client

import io.cloudevents.CloudEvent
import io.quarkus.rest.client.reactive.NotBody
import io.quarkus.rest.client.reactive.Url
import jakarta.ws.rs.POST
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@RegisterRestClient(configKey = "notification-api")
interface NotificationClient {
    @POST
    @ClientHeaderParam(name = "aeg-sas-key", value = ["{notificationTopicAccessKey}"])
    fun sendNotification(
        @Url url: String?,
        event: CloudEvent,
        @NotBody notificationTopicAccessKey: String
    )
}