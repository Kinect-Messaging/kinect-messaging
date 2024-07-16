package com.kinectmessaging.email.client

import com.kinectmessaging.libs.common.Defaults
import com.kinectmessaging.libs.model.KTemplate
import com.kinectmessaging.libs.model.TemplatePersonalizationRequest
import io.netty.handler.logging.LogLevel
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import reactor.netty.http.client.HttpClient
import reactor.netty.transport.logging.AdvancedByteBufFormat
import java.util.*


var httpClient = HttpClient
    .create()
    .wiretap("reactor.netty.http.client.HttpClient",
        LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)

@Component
class TemplateClient (private val webClient: WebClient) {
    @Value("\${app.client.template.apiKey}")
    lateinit var apiKey: String
    suspend fun loadTemplate(personalizationRequest: TemplatePersonalizationRequest): List<KTemplate>? =
        webClient
            .post()
            .header(Defaults.TRANSACTION_ID_HEADER, MDC.get("transaction-id") ?: UUID.randomUUID().toString())
            .header("Ocp-Apim-Subscription-Key", apiKey)
            .bodyValue(personalizationRequest)
            .retrieve()
            .awaitBody<List<KTemplate>>()
}

@Configuration
class Config{

    @Value("\${app.client.template.url}")
    lateinit var url: String

    @Bean
    fun webClient(builder: WebClient.Builder): WebClient =
        builder
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .baseUrl(url)
            .build()
}
