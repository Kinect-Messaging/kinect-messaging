package com.kinectmessaging.email.client

import com.kinectmessaging.libs.common.Defaults
import com.kinectmessaging.libs.model.ContactMessages
import com.kinectmessaging.libs.model.KTemplate
import com.kinectmessaging.libs.model.TemplatePersonalizationRequest
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodilessEntity
import org.springframework.web.reactive.function.client.awaitBody
import reactor.netty.http.client.HttpClient
import java.util.*


var httpClient: HttpClient = HttpClient
    .create()
//    .wiretap("reactor.netty.http.client.HttpClient",
//        LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)

@Component
class ApiClient () {

    @Autowired
    lateinit var templateWebClient: WebClient

    @Autowired
    lateinit var contactHistoryWebClient: WebClient

    suspend fun loadTemplate(personalizationRequest: TemplatePersonalizationRequest): List<KTemplate>? =
        templateWebClient
            .post()
            .header(Defaults.TRANSACTION_ID_HEADER, MDC.get(Defaults.TRANSACTION_ID_HEADER) ?: UUID.randomUUID().toString())
            .bodyValue(personalizationRequest)
            .retrieve()
            .awaitBody<List<KTemplate>>()

    suspend fun updateContactMessages(contactMessages: ContactMessages): ResponseEntity<Void> =
        contactHistoryWebClient
            .post()
            .header(Defaults.TRANSACTION_ID_HEADER, MDC.get(Defaults.TRANSACTION_ID_HEADER) ?: UUID.randomUUID().toString())
            .bodyValue(contactMessages)
            .retrieve()
            .awaitBodilessEntity()
}

@Configuration
class Config{

    @Value("\${app.client.template.url}")
    lateinit var templateUrl: String

    @Value("\${app.client.contact-history.url}")
    lateinit var contactHistoryUrl: String

    @Bean
    fun templateWebClient(builder: WebClient.Builder): WebClient =
        builder
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .baseUrl(templateUrl)
            .build()

    @Bean
    fun contactHistoryWebClient(builder: WebClient.Builder): WebClient =
        builder
            .clone()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .baseUrl(contactHistoryUrl)
            .build()
}
