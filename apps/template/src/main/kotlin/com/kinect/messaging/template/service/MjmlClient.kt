package com.kinect.messaging.template.service

import com.kinect.messaging.template.model.MjmlRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClient.Builder
import org.springframework.web.reactive.function.client.awaitBody

@Component
class MjmlClient (private val webClient: WebClient){
     suspend fun renderMjmlToHtml(mjmlRequest: MjmlRequest): String =
        webClient
            .post()
            .bodyValue(mjmlRequest)
            .retrieve()
            .awaitBody<String>()

}

@Configuration
class Config{

    @Value("\${app.endpoint.mjml.url}")
    lateinit var url: String

    @Value("\${app.endpoint.mjml.api-key}")
    lateinit var apiKey: String

    @Bean
    fun webClient(builder: Builder): WebClient =
        builder
            .baseUrl(url)
            .defaultHeaders { httpHeaders ->
                httpHeaders.add("Ocp-Apim-Subscription-Key", apiKey)
            }
            .build()
}