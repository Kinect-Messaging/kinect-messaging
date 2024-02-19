package com.kinect.messaging.config.controller

import com.azure.spring.data.cosmos.core.query.CosmosPageRequest
import com.kinect.messaging.config.model.JourneyEntity
import com.kinect.messaging.config.repository.JourneyRepository
import com.kinect.messaging.libs.model.Audit
import com.kinect.messaging.libs.model.JourneyConfig
import com.kinect.messaging.libs.model.JourneySteps
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@AutoConfigureMockMvc
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JourneyControllerTest {

    private final val baseUrl = "/kinect/messaging/config/journey"

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockBean
    lateinit var journeyRepository: JourneyRepository

    /*val messageConfig = mutableListOf(
        MessageConfig(
            messageName = "Customer Created",
            messageCondition = "customer.status='new' and customer.email != null",
            messageStatus = MessageStatus.DRAFT,
            messageVersion = 1,
            emailConfig = mutableListOf(
                EmailConfig(
                    targetSystem = TargetSystem.AZURE_COMMUNICATION_SERVICE,
                    subject = "Welcome to Kinect Messaging",
                    senderAddress = "welcome@kinectmessaging.com",
                    toRecipients = mutableListOf(
                        InternetAddress("test@kinectmessaging.com")
                    ),
                    templateConfig = mutableMapOf(Pair("template_1", "text"), Pair("template_2", "html")),
                    emailHeaders = null
                )
            )
        )
    )*/

    val messageConfig = mutableMapOf(Pair("1", "Customer Created"), Pair("2", "Customer Welcome"))


    val auditInfo = Audit(
        createdBy = "Unit Test 1",
        createdTime = Calendar.getInstance().time.toString(),
        updatedBy = "Unit Test 1",
        updatedTime = Calendar.getInstance().time.toString()
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
        val mockData = JourneyEntity(
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
        val mockData = JourneyEntity(
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
        val pagingOptions = CosmosPageRequest(pageNo, pageSize, sortBy)

        // mock the database response
        val mockData = PageImpl(
            mutableListOf(
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
        )
        given(journeyRepository.findAll(any(CosmosPageRequest::class.java))).willReturn(mockData)

        // when API is invoked, then return valid response
        webTestClient.get()
            .uri("$baseUrl?pageNo=$pageNo&pageSize=$pageSize&sortBy=$sortBy")
            .header("X-Transaction-Id", UUID.randomUUID().toString())
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("\$[0].journeyName").isEqualTo("Welcome Customer Journey")
            .jsonPath("\$[1].journeyName").isEqualTo("Customer Subscription Journey")

    }
}