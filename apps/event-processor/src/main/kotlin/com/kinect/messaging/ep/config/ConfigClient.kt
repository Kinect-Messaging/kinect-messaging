package com.kinect.messaging.ep.config

import com.kinect.messaging.libs.model.JourneyConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodyOrNull

@Component
class ConfigClient (private val webClient: WebClient) {
    suspend fun getConfigForEvent(eventName: String): List<JourneyConfig>? =
        webClient
            .get()
            .uri("?eventName=$eventName")
            .retrieve()
            .awaitBodyOrNull<List<JourneyConfig>>()
}

@Configuration
class Config{

    @Value("\${app.endpoint.config.url}")
    lateinit var url: String

    @Value("\${app.endpoint.config.username}")
    lateinit var userName: String

    @Value("\${app.endpoint.config.password}")
    lateinit var password: String

    @Bean
    fun webClient(builder: WebClient.Builder): WebClient =
        builder
            .baseUrl(url)
            .defaultHeaders { httpHeaders ->
                httpHeaders.setBasicAuth(userName,password)
            }
            .build()
}