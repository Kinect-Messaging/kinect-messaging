package com.kinect.messaging.config.controller

import com.azure.spring.data.cosmos.core.query.CosmosPageRequest
import com.kinect.messaging.config.model.MessageEntity
import com.kinect.messaging.config.repository.MessageRepository
import com.kinect.messaging.libs.model.*
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
import javax.mail.internet.InternetAddress

@AutoConfigureMockMvc
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessageControllerTest {

    private final val baseUrl = "/kinect/messaging/config/message"

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockBean
    lateinit var messageRepository: MessageRepository

    private final val auditInfo = Audit(
        createdBy = "Unit Test 1",
        createdTime = Calendar.getInstance().time.toString(),
        updatedBy = "Unit Test 1",
        updatedTime = Calendar.getInstance().time.toString()
    )

    val messageConfig = mutableListOf(
        MessageConfig(
            messageId = "1",
            messageName = "Customer Created",
            messageCondition = "customer.status='new' and customer.email != null",
            messageStatus = MessageStatus.DRAFT,
            messageVersion = 1,
            journeyId = "journey_1",
            auditInfo = auditInfo,
            emailConfig = mutableListOf(
                EmailConfig(
                    targetSystem = TargetSystem.AZURE_COMMUNICATION_SERVICE,
                    subject = "Welcome to Kinect Messaging",
                    senderAddress = "welcome@kinectmessaging.com",
                    toRecipients = mutableListOf(
                        EmailRecipientConfig("customer.firstName", "customer.lastName","customer.email")
                    ),
                    templateConfig = mutableMapOf(Pair("template_1", "text"), Pair("template_2", "html")),
                    emailHeaders = null
                )
            )
        )
    )

    val messageEntity = mutableListOf(
        MessageEntity(
            messageId = "1",
            messageName = "Customer Created",
            messageCondition = "customer.status='new' and customer.email != null",
            messageStatus = MessageStatus.DRAFT,
            messageVersion = 1,
            journeyId = "journey_1",
            auditInfo = auditInfo,
            emailConfig = mutableListOf(
                EmailConfig(
                    targetSystem = TargetSystem.AZURE_COMMUNICATION_SERVICE,
                    subject = "Welcome to Kinect Messaging",
                    senderAddress = "welcome@kinectmessaging.com",
                    toRecipients = mutableListOf(
                        EmailRecipientConfig("customer.firstName", "customer.lastName","customer.email")
                    ),
                    templateConfig = mutableMapOf(Pair("template_1", "text"), Pair("template_2", "html")),
                    emailHeaders = null
                )
            )
        ),
        MessageEntity(
            messageId = "2",
            messageName = "Customer Welcome",
            messageCondition = "customer.status='new' and customer.email != null",
            messageStatus = MessageStatus.DRAFT,
            messageVersion = 1,
            journeyId = "journey_1",
            auditInfo = auditInfo,
            emailConfig = mutableListOf(
                EmailConfig(
                    targetSystem = TargetSystem.AZURE_COMMUNICATION_SERVICE,
                    subject = "Welcome to Kinect Messaging",
                    senderAddress = "welcome@kinectmessaging.com",
                    toRecipients = mutableListOf(
                        EmailRecipientConfig("customer.firstName", "customer.lastName","customer.email")
                    ),
                    templateConfig = mutableMapOf(Pair("template_1", "text"), Pair("template_2", "html")),
                    emailHeaders = null
                )
            )
        )
    )




    @Test
    fun createMessage() {
        // given
        val givenInput = messageConfig[0]

        // mock the database response
        val mockData = messageEntity[0]

        given(messageRepository.save(mockData)).willReturn(mockData)

        //when API is invoked, then valid response is returned
        webTestClient.post()
            .uri(baseUrl)
            .bodyValue(givenInput)
            .header("X-Transaction-Id", UUID.randomUUID().toString())
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("\$.messageName").isEqualTo("Customer Created")

    }

    @Test
    fun getMessageById() {

        // given
        val givenInput = "1"

        // mock the database response
        val mockData = messageEntity[0]
        given(messageRepository.findById(givenInput)).willReturn(Optional.of(mockData))

        //when API is invoked, then valid response is returned
        webTestClient.get()
            .uri("$baseUrl/$givenInput")
            .header("X-Transaction-Id", UUID.randomUUID().toString())
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("\$.messageName").isEqualTo("Customer Created")

    }

    @Test
    fun getMessages() {

        // given
        val pageNo = 1
        val pageSize = 1
        val sortBy = "messageName"
        val pagingOptions = CosmosPageRequest(pageNo, pageSize, sortBy)

        // mock the database response
        val mockData = PageImpl(
            messageEntity
        )
        given(messageRepository.findAll(any(CosmosPageRequest::class.java))).willReturn(mockData)

        // when API is invoked, then return valid response
        webTestClient.get()
            .uri("$baseUrl?pageNo=$pageNo&pageSize=$pageSize&sortBy=$sortBy")
            .header("X-Transaction-Id", UUID.randomUUID().toString())
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("\$[0].messageName").isEqualTo("Customer Created")
            .jsonPath("\$[1].messageName").isEqualTo("Customer Welcome")

    }
}