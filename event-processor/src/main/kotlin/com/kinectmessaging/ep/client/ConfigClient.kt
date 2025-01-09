package com.kinectmessaging.ep.client

import com.kinectmessaging.libs.model.JourneyConfig
import com.kinectmessaging.libs.model.MessageConfig
import io.quarkus.rest.client.reactive.NotBody
import io.quarkus.rest.client.reactive.Url
import jakarta.ws.rs.GET
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@RegisterRestClient(configKey = "config-api")
interface ConfigClient {
    @GET
    fun getJourneyConfigsByEventName(
        @NotBody @Url url: String,
    ): List<JourneyConfig>?

    @GET
    fun getMessageConfigsById(
        @NotBody @Url url: String,
    ): MessageConfig?
}