package com.kinectmessaging.config.controller

import com.kinectmessaging.config.model.MessageEntity
import com.kinectmessaging.config.repository.MessageRepository
import com.kinectmessaging.libs.model.*
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime
import java.util.*

@AutoConfigureMockMvc
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessageControllerTest {

    private final val baseUrl = "/kinect/messaging/config/message"

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockitoBean
    lateinit var messageRepository: MessageRepository

    private final val auditInfo = Audit(
        createdBy = "Unit Test 1",
        createdTime = LocalDateTime.now().toString(),
        updatedBy = "Unit Test 1",
        updatedTime = LocalDateTime.now().toString()
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

    val messageEntities = mutableListOf(
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
        val mockData = messageEntities[0]

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
        val mockData = messageEntities[0]
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

        // mock the database response
        val mockData = PageImpl(
            messageEntities
        )
        given(messageRepository.findAll(any(Pageable::class.java))).willReturn(mockData)

        // when API is invoked, then return valid response
        webTestClient.get()
            .uri("$baseUrl?_start=$pageNo&_end=$pageSize&_sort=$sortBy&_order=ASC")
            .header("X-Transaction-Id", UUID.randomUUID().toString())
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("\$[0].messageName").isEqualTo("Customer Created")
            .jsonPath("\$[1].messageName").isEqualTo("Customer Welcome")

    }
}