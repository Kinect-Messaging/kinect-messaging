package com.kinectmessaging.ep.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.kinectmessaging.ep.client.ApiClient
import com.kinectmessaging.libs.model.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.anyString
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.io.File
import java.util.*


@AutoConfigureMockMvc
@AutoConfigureWebTestClient(timeout = "36000")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EventProcessorControllerTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockBean
    lateinit var apiClient: ApiClient

    private final val baseUrl = "/kinect/messaging/event"


    private val mockJourneyResponse = "{\n" +
            "    \"journeyId\": \"7b4f1a80-aec0-41ae-967c-a14f543b909a\",\n" +
            "    \"journeyName\": \"Customer Contact Journey\",\n" +
            "    \"journeySteps\": [\n" +
            "        {\n" +
            "            \"seqId\": 1,\n" +
            "            \"eventName\": \"CustomerSupportRequested\",\n" +
            "            \"stepCondition\": null,\n" +
            "            \"messageConfigs\": {\n" +
            "                \"517b5eb0-33c3-4779-88a5-eb333a0350a\": \"Kinect_ContactForm_Support\",\n" +
            "                \"499a34eb-70c4-4fa2-b5fb-0a0635ad7813\": \"Kinect_ContactForm_Customer\"\n" +
            "            }\n" +
            "        }\n" +
            "    ],\n" +
            "    \"auditInfo\": {\n" +
            "        \"createdBy\": \"System\",\n" +
            "        \"createdTime\": \"2024-10-06T04:13:05.114Z\",\n" +
            "        \"updatedBy\": \"System\",\n" +
            "        \"updatedTime\": \"2024-10-06T04:13:05.114Z\"\n" +
            "    }\n" +
            "}"

    private val mockMessageResponse1 = "{\n" +
            "    \"messageId\": \"517b5eb0-33c3-4779-88a5-eb333a0350ab\",\n" +
            "    \"messageName\": \"Kinect_ContactForm_Support\",\n" +
            "    \"messageVersion\": 1,\n" +
            "    \"messageCondition\": null,\n" +
            "    \"messageStatus\": \"DEV\",\n" +
            "    \"emailConfig\": [\n" +
            "        {\n" +
            "            \"targetSystem\": \"AZURE_COMMUNICATION_SERVICE\",\n" +
            "            \"emailHeaders\": null,\n" +
            "            \"senderAddress\": null,\n" +
            "            \"subject\": \"'New Contact Form Submission'\",\n" +
            "            \"toRecipients\": [\n" +
            "                {\n" +
            "                    \"firstName\": \"customer.firstName\",\n" +
            "                    \"lastName\": \"customer.lastName\",\n" +
            "                    \"emailAddress\": \"customer.email\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"ccRecipients\": null,\n" +
            "            \"bccRecipients\": null,\n" +
            "            \"replyTo\": null,\n" +
            "            \"attachments\": null,\n" +
            "            \"personalizationData\": {\n" +
            "                \"formData\": {\n" +
            "                    \"name\": \"customer.firstName\",\n" +
            "                    \"email\": \"customer.email\",\n" +
            "                    \"phone\": \"customer.phone\",\n" +
            "                    \"reason\": \"contactReason\"\n" +
            "                }\n" +
            "            },\n" +
            "            \"templateConfig\": {\n" +
            "                \"textTemplate_contactForm_kinect\": \"text\",\n" +
            "                \"htmlTemplate_contactForm_kinect\": \"html\"\n" +
            "            }\n" +
            "        }\n" +
            "    ],\n" +
            "    \"journeyId\": \"7b4f1a80-aec0-41ae-967c-a14f543b909a\",\n" +
            "    \"auditInfo\": {\n" +
            "        \"createdBy\": \"System\",\n" +
            "        \"createdTime\": \"2024-10-06T04:12:43.707Z\",\n" +
            "        \"updatedBy\": \"System\",\n" +
            "        \"updatedTime\": \"2024-10-06T04:12:43.707Z\"\n" +
            "    }\n" +
            "}"

    private val mockMessageResponse2 = "{\n" +
            "    \"messageId\": \"499a34eb-70c4-4fa2-b5fb-0a0635ad7813\",\n" +
            "    \"messageName\": \"Kinect_ContactForm_Customer\",\n" +
            "    \"messageVersion\": 1,\n" +
            "    \"messageCondition\": null,\n" +
            "    \"messageStatus\": \"DEV\",\n" +
            "    \"emailConfig\": [\n" +
            "        {\n" +
            "            \"targetSystem\": \"AZURE_COMMUNICATION_SERVICE\",\n" +
            "            \"emailHeaders\": null,\n" +
            "            \"senderAddress\": null,\n" +
            "            \"subject\": \"'Thank You for Contacting Us!'\",\n" +
            "            \"toRecipients\": [\n" +
            "                {\n" +
            "                    \"firstName\": \"customer.firstName\",\n" +
            "                    \"lastName\": \"customer.lastName\",\n" +
            "                    \"emailAddress\": \"customer.email\"\n" +
            "                }\n" +
            "            ],\n" +
            "            \"ccRecipients\": null,\n" +
            "            \"bccRecipients\": null,\n" +
            "            \"replyTo\": null,\n" +
            "            \"attachments\": null,\n" +
            "            \"personalizationData\": {\n" +
            "                \"formData\": {\n" +
            "                    \"name\": \"customer.firstName\",\n" +
            "                    \"email\": \"customer.email\",\n" +
            "                    \"phone\": \"customer.phone\",\n" +
            "                    \"reason\": \"contactReason\"\n" +
            "                }\n" +
            "            },\n" +
            "            \"templateConfig\": {\n" +
            "                \"textTemplate_contactForm_customer\": \"text\",\n" +
            "                \"htmlTemplate_contactForm_customer\": \"html\"\n" +
            "            }\n" +
            "        }\n" +
            "    ],\n" +
            "    \"journeyId\": \"7b4f1a80-aec0-41ae-967c-a14f543b909a\",\n" +
            "    \"auditInfo\": {\n" +
            "        \"createdBy\": \"System\",\n" +
            "        \"createdTime\": \"2024-10-06T04:08:19.375Z\",\n" +
            "        \"updatedBy\": \"System\",\n" +
            "        \"updatedTime\": \"2024-10-06T04:08:19.375Z\"\n" +
            "    }\n" +
            "}"
    /**
     * Validate notifications triggered with valid event data
     */
    @Test
    fun `given Event Data when valid Email Config then trigger Notifications`() {
        val payload = jacksonObjectMapper().readTree(File("/Users/raj/Work/Springboot/kinect-messaging/apps/email/src/test/resources/test_input_event_email_1.json"))
        //given
        val givenInput = KEvent(
            eventId = UUID.randomUUID().toString(),
            eventName = "CustomerSupportRequested",
            eventTime = Calendar.getInstance().time,
            payload = payload,
            recipients = mutableListOf(
                Person(
                firstName = "customer.firstName",
                lastName = "customer.lastName",
                contacts = mutableListOf(Contact(
                    email = "customer.email",
                    phone = "customer.phone",
                    address = Address(
                        addressLine1 = "customer.address.addressLine1",
                        addressLine2 = null,
                        city = "customer.address.city",
                        state = "customer.address.state",
                        postalCode = "customer.address.zip",
                        country = "customer.address.country",
                    )
                )),
                preferredLanguage = mutableMapOf(Pair(TemplateLanguage.EN, 1)),
            )
            )
        )

        runBlocking {
            // mock client calls
            given(apiClient.getJourneyConfigsByEventName(anyString()))
                .willReturn(listOf(jacksonObjectMapper().readValue(mockJourneyResponse)))

            given(apiClient.getMessageConfigsById(anyString()))
                .willAnswer {
                    val argument = it.arguments[0]
                    when (argument) {
                        "517b5eb0-33c3-4779-88a5-eb333a0350a" -> jacksonObjectMapper().readValue<MessageConfig>(mockMessageResponse1)
                        "499a34eb-70c4-4fa2-b5fb-0a0635ad7813" -> jacksonObjectMapper().readValue<MessageConfig>(mockMessageResponse2)
                        else -> null
                    }
                }

//            given(apiClient.sendEmail(any(KMessage::class.java))).willReturn("Success")

            //when
            val actualResult = webTestClient.post()
                .uri(baseUrl)
                .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(givenInput)
                .exchange()
                .expectStatus().isOk
                .expectBody(String::class.java)
                .returnResult()
                .responseBody

            //then
            assert(actualResult?.contains("Total notifications sent") == true)

        }
    }
}