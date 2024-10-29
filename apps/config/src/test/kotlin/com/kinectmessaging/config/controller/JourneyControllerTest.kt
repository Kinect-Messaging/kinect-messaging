package com.kinectmessaging.config.controller

import com.kinectmessaging.config.model.JourneyEntity
import com.kinectmessaging.config.repository.JourneyRepository
import com.kinectmessaging.libs.model.Audit
import com.kinectmessaging.libs.model.JourneyConfig
import com.kinectmessaging.libs.model.JourneySteps
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime
import java.util.*

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JourneyControllerTest {

    private final val baseUrl = "/kinect/messaging/config/journey"

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockBean
    lateinit var journeyRepository: JourneyRepository

    private final val messageConfig = mutableMapOf(Pair("1", "Customer Created"), Pair("2", "Customer Welcome"))


    private final val auditInfo = Audit(
        createdBy = "Unit Test 1",
        createdTime = LocalDateTime.now().toString(),
        updatedBy = "Unit Test 1",
        updatedTime = LocalDateTime.now().toString()
    )

    val journeyEntities = mutableListOf(
        JourneyEntity(
            journeyId = "1",
            journeyName = "Welcome Customer Journey",
            journeySteps = mutableListOf(
                JourneySteps(
                    seqId = 1,
                    eventName = "CustomerCreated",
                    stepCondition = "customer.stats='new'",
                    messageConfigs = messageConfig
                )
            ),
            auditInfo = auditInfo
        ),
        JourneyEntity(
            journeyId = "2",
            journeyName = "Customer Subscription Journey",
            journeySteps = mutableListOf(
                JourneySteps(
                    seqId = 1,
                    eventName = "CustomerCreated",
                    stepCondition = "customer.subscriptions.size > 0",
                    messageConfigs = messageConfig
                )
            ),
            auditInfo = auditInfo
        )
    )

    @Test
    fun createJourney() {
        // given
        val givenInput = JourneyConfig(
            journeyId = "1",
            journeyName = "Welcome Customer Journey",
            journeySteps = mutableListOf(
                JourneySteps(
                    seqId = 1,
                    eventName = "CustomerCreated",
                    stepCondition = "customer.stats='new'",
                    messageConfigs = messageConfig
                )
            ),
            auditInfo = auditInfo
        )

        // mock the database response
        val mockData = journeyEntities[0]
        given(journeyRepository.save(mockData)).willReturn(mockData)

        //when API is invoked, then valid response is returned
        webTestClient.post()
            .uri(baseUrl)
            .bodyValue(givenInput)
            .header("X-Transaction-Id", UUID.randomUUID().toString())
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("\$.journeyName").isEqualTo("Welcome Customer Journey")

    }

    @Test
    fun getJourneyById() {

        // given
        val givenInput = "1"

        // mock the database response
        val mockData = journeyEntities[0]
        given(journeyRepository.findById(givenInput)).willReturn(Optional.of(mockData))

        //when API is invoked, then valid response is returned
        webTestClient.get()
            .uri("$baseUrl/$givenInput")
            .header("X-Transaction-Id", UUID.randomUUID().toString())
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("\$.journeyName").isEqualTo("Welcome Customer Journey")

    }

    @Test
    fun getJourneys() {

        // given
        val pageNo = 1
        val pageSize = 1
        val sortBy = "journeyId"

        // mock the database response

        val mockData = PageImpl(
            journeyEntities
        )
        given(journeyRepository.findAll(any(Pageable::class.java))).willReturn(mockData)

        // when API is invoked, then return valid response
        webTestClient.get()
            .uri("$baseUrl?_start=$pageNo&_end=$pageSize&_sort=$sortBy&_order=ASC")
            .header("X-Transaction-Id", UUID.randomUUID().toString())
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("\$[0].journeyName").isEqualTo("Welcome Customer Journey")
            .jsonPath("\$[1].journeyName").isEqualTo("Customer Subscription Journey")

    }

    @Test
    fun getJourneysByEventName() {

        // given
        val eventName = "TestEvent"

        // mock the database response

        given(journeyRepository.findAllByJourneySteps_EventName(eventName)).willReturn(journeyEntities)

        // when API is invoked, then return valid response
        webTestClient.get()
            .uri("$baseUrl/?eventName=$eventName")
            .header("X-Transaction-Id", UUID.randomUUID().toString())
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("\$[0].journeyName").isEqualTo("Welcome Customer Journey")
            .jsonPath("\$[1].journeyName").isEqualTo("Customer Subscription Journey")

    }
}