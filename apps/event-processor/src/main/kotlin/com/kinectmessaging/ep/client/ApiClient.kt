package com.kinectmessaging.ep.client

import com.kinectmessaging.libs.common.CloudEventsHeaders
import com.kinectmessaging.libs.common.Defaults
import com.kinectmessaging.libs.model.JourneyConfig
import com.kinectmessaging.libs.model.KContactHistory
import com.kinectmessaging.libs.model.KMessage
import com.kinectmessaging.libs.model.MessageConfig
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
import org.springframework.web.reactive.function.client.awaitBodyOrNull
import reactor.netty.http.client.HttpClient
import java.time.LocalDateTime
import java.util.*


var httpClient: HttpClient = HttpClient
    .create()
//    .wiretap("reactor.netty.http.client.HttpClient",
//        LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
@Component
class ApiClient () {

    @Autowired
    lateinit var journeyWebClient: WebClient

    @Autowired
    lateinit var messageWebClient: WebClient

    @Autowired
    lateinit var emailWebClient: WebClient

    @Autowired
    lateinit var contactHistoryWebClient: WebClient

    @Value("\${app.cloud-events.headers.spec-version}")
    lateinit var cloudEventsSpecVersion: String

    @Value("\${app.cloud-events.headers.type}")
    lateinit var cloudEventsType: String

    @Value("\${app.cloud-events.headers.source}")
    lateinit var cloudEventsSource: String

    @Value("\${app.client.contact-history.access-key}")
    lateinit var contactHistoryTopicAccessKey: String

    suspend fun getJourneyConfigsByEventName(eventName: String): List<JourneyConfig>? =
        journeyWebClient
            .get()
            .uri("/?eventName=$eventName")
            .header(Defaults.TRANSACTION_ID_HEADER, MDC.get(Defaults.TRANSACTION_ID_HEADER) ?: UUID.randomUUID().toString())
            .retrieve()
            .awaitBodyOrNull<List<JourneyConfig>>()

    suspend fun getMessageConfigsById(id: String): MessageConfig? =
        messageWebClient
            .get()
            .uri("/$id")
            .header(Defaults.TRANSACTION_ID_HEADER, MDC.get(Defaults.TRANSACTION_ID_HEADER) ?: UUID.randomUUID().toString())
            .retrieve()
            .awaitBodyOrNull<MessageConfig>()

    suspend fun sendEmail(emailData: KMessage): String =
        emailWebClient
            .post()
            .header(Defaults.TRANSACTION_ID_HEADER, MDC.get(Defaults.TRANSACTION_ID_HEADER) ?: UUID.randomUUID().toString())
            .bodyValue(emailData)
            .retrieve()
            .awaitBody<String>()

    suspend fun createContactHistory(contactHistory: KContactHistory): ResponseEntity<Void> =
        contactHistoryWebClient
            .post()
            .header(Defaults.TRANSACTION_ID_HEADER, MDC.get(Defaults.TRANSACTION_ID_HEADER) ?: UUID.randomUUID().toString())
            .header("aeg-sas-key", contactHistoryTopicAccessKey)
            .headers {
                it.set(CloudEventsHeaders.ID, contactHistory.id)
                it.set(CloudEventsHeaders.SPEC_VERSION, cloudEventsSpecVersion)
                it.set(CloudEventsHeaders.TYPE, cloudEventsType)
                it.set(CloudEventsHeaders.SOURCE, cloudEventsSource)
                it.set(CloudEventsHeaders.TIME, LocalDateTime.now().toString())
            }
            .bodyValue(contactHistory)
            .retrieve()
            .awaitBodilessEntity()
}

@Configuration
class Config{

    @Value("\${app.client.journey.url}")
    lateinit var journeyUrl: String
    @Value("\${app.client.message.url}")
    lateinit var messageUrl: String
    @Value("\${app.client.email.url}")
    lateinit var emailUrl: String
    @Value("\${app.client.contact-history.url}")
    lateinit var contactHistoryUrl: String

    @Bean
    fun journeyWebClient(builder: WebClient.Builder): WebClient =
        builder
            .clone()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .baseUrl(journeyUrl)
            .build()

    @Bean
    fun messageWebClient(builder: WebClient.Builder): WebClient =
        builder
            .clone()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .baseUrl(messageUrl)
            .build()

    @Bean
    fun emailWebClient(builder: WebClient.Builder): WebClient =
        builder
            .clone()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .baseUrl(emailUrl)
            .build()

    @Bean
    fun contactHistoryWebClient(builder: WebClient.Builder): WebClient =
        builder
            .clone()
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .baseUrl(contactHistoryUrl)
            .build()
}