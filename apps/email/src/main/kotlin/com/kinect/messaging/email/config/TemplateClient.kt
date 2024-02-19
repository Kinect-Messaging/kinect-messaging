package com.kinect.messaging.email.config

import com.kinect.messaging.libs.model.KTemplate
import com.kinect.messaging.libs.model.TemplatePersonalizationRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class TemplateClient (private val webClient: WebClient) {
    suspend fun loadTemplate(personalizationRequest: TemplatePersonalizationRequest): List<KTemplate>? =
        webClient
            .post()
            .bodyValue(personalizationRequest)
            .retrieve()
            .awaitBody<List<KTemplate>>()
}

@Configuration
class Config{

    @Value("\${app.endpoint.template.url}")
    lateinit var url: String

    @Value("\${app.endpoint.template.username}")
    lateinit var userName: String

    @Value("\${app.endpoint.template.password}")
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