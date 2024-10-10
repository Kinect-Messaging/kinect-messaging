package com.kinectmessaging.email.client

import com.kinectmessaging.libs.common.Defaults
import com.kinectmessaging.libs.model.KTemplate
import com.kinectmessaging.libs.model.TemplatePersonalizationRequest
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import reactor.netty.http.client.HttpClient
import java.util.*


var httpClient: HttpClient = HttpClient
    .create()
//    .wiretap("reactor.netty.http.client.HttpClient",
//        LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)

@Component
class TemplateClient (private val webClient: WebClient) {

    suspend fun loadTemplate(personalizationRequest: TemplatePersonalizationRequest): List<KTemplate>? =
        webClient
            .post()
            .header(Defaults.TRANSACTION_ID_HEADER, MDC.get("transaction-id") ?: UUID.randomUUID().toString())
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
