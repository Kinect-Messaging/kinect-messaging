package com.kinectmessaging.ep.client

import com.kinectmessaging.libs.common.CloudEventsHeaders
import com.kinectmessaging.libs.model.KMessage
import io.quarkus.rest.client.reactive.NotBody
import io.quarkus.rest.client.reactive.Url
import jakarta.ws.rs.POST
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@RegisterRestClient(configKey = "notification-api")
interface NotificationClient {
    @POST
    @ClientHeaderParam(name = "aeg-sas-key", value = ["\${app.client.notification.access-key}"])
    @ClientHeaderParam(name = CloudEventsHeaders.SPEC_VERSION, value = ["\${app.cloud-events.headers.spec-version}"])
    @ClientHeaderParam(name = CloudEventsHeaders.TYPE, value = ["\${app.cloud-events.headers.notification.type}"])
    @ClientHeaderParam(name = CloudEventsHeaders.SOURCE, value = ["\${app.cloud-events.headers.notification.source}"])
    @ClientHeaderParam(name = CloudEventsHeaders.TIME, value = ["{eventTime}"])
    @ClientHeaderParam(name = CloudEventsHeaders.ID, value = ["{eventId}"])
    fun sendNotification(
        @NotBody @Url url: String?,
        @NotBody eventId: String,
        @NotBody eventTime: String,
        event: KMessage
    )
}