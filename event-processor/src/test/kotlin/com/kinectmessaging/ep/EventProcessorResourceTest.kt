package com.kinectmessaging.ep

import com.fasterxml.jackson.databind.ObjectMapper
import com.kinectmessaging.ep.client.ConfigClient
import com.kinectmessaging.ep.client.ContactHistoryClient
import com.kinectmessaging.ep.client.NotificationClient
import com.kinectmessaging.libs.model.*
import io.cloudevents.CloudEvent
import io.mockk.every
import io.quarkiverse.test.junit.mockk.InjectMock
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import jakarta.inject.Inject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.http.HttpStatus
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*

@QuarkusTest
class EventProcessorResourceTest (){

    @Inject
    private lateinit var mapper: ObjectMapper
    private final val baseUrl = "/kinect/messaging/event"

    @InjectMock
    @field:RestClient
    private lateinit var configClient: ConfigClient

    @InjectMock
    @field:RestClient
    private lateinit var contactHistoryClient: ContactHistoryClient

    @InjectMock
    @field:RestClient
    lateinit var notificationClient: NotificationClient

    val testPayload = object {}.javaClass.getResourceAsStream("/test_input_event_email_1.json")?.bufferedReader()?.readText()

    private val mockJourneyResponse = "[{\n" +
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
            "}]"

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

    @Test
    fun `given Event Data when valid Email Config with Payload recipients then trigger Notifications`() {
        val payload = testPayload?.let { mapper.readTree(it) }
        //given
        val givenInput = KEvent(
            eventId = UUID.randomUUID().toString(),
            eventName = "CustomerSupportRequested",
            eventTime = LocalDateTime.now(),
            payload = payload,
            recipients = mutableListOf(
                Person(
                    firstName = "Kinect",
                    lastName = "Tester",
                    contacts = mutableListOf(
                        Contact(
                        email = "kinecttester@yopmail.com",
                        phone = "123-456-7890",
                        address = Address(
                            addressLine1 = "customer.address.addressLine1",
                            addressLine2 = null,
                            city = "customer.address.city",
                            state = "customer.address.state",
                            postalCode = "customer.address.zip",
                            country = "customer.address.country",
                        )
                    )
                    ),
                    preferredLanguage = mutableMapOf(Pair(Language.EN, 1)),
                )
            )
        )

        every { configClient.getJourneyConfigsByEventName(any(String::class)) }
            .returns(Json.decodeFromString<List<JourneyConfig>>(mockJourneyResponse))

        every { configClient.getMessageConfigsById("/kinect/messaging/config/message/517b5eb0-33c3-4779-88a5-eb333a0350a") }
            .returns(Json.decodeFromString<MessageConfig>(mockMessageResponse1))

        every { configClient.getMessageConfigsById("/kinect/messaging/config/message/499a34eb-70c4-4fa2-b5fb-0a0635ad7813") }
            .returns(Json.decodeFromString<MessageConfig>(mockMessageResponse2))

        every { contactHistoryClient.createContactHistory(any(String::class), any(ByteArray::class)) }.returns(Unit)

        every { notificationClient.sendNotification(any(String::class), any(ByteArray::class)) }
            .returns(Unit)

        val requestInput = Json.encodeToString(givenInput)
        // call a REST endpoint that sends events
        val response = given()
            .header("Content-Type", ContentType.JSON)
            .body(requestInput)
            .`when`()
            .post(baseUrl)
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract().body()

        Assertions.assertEquals( true, response.asString().contains("Total notifications sent - 2"),)
    }

    @Test
    fun `given Event Data when valid Email Config with Config recipients then trigger Notifications`() {
        val payload = testPayload?.let { mapper.readTree(it) }
        //given
        val givenInput = KEvent(
            eventId = UUID.randomUUID().toString(),
            eventName = "CustomerSupportRequested",
            eventTime = LocalDateTime.now(),
            payload = payload,
            recipients = null
        )

        every { configClient.getJourneyConfigsByEventName(any(String::class)) }
            .returns(Json.decodeFromString<List<JourneyConfig>>(mockJourneyResponse))

        every { configClient.getMessageConfigsById("/kinect/messaging/config/message/517b5eb0-33c3-4779-88a5-eb333a0350a") }
            .returns(Json.decodeFromString<MessageConfig>(mockMessageResponse1))

        every { configClient.getMessageConfigsById("/kinect/messaging/config/message/499a34eb-70c4-4fa2-b5fb-0a0635ad7813") }
            .returns(Json.decodeFromString<MessageConfig>(mockMessageResponse2))

        every { contactHistoryClient.createContactHistory(any(String::class), any(ByteArray::class)) }.returns(Unit)

        every { notificationClient.sendNotification(any(String::class), any(ByteArray::class)) }
            .returns(Unit)

        val requestInput = Json.encodeToString(givenInput)
        // call a REST endpoint that sends events
        val response = given()
            .header("Content-Type", ContentType.JSON)
            .body(requestInput)
            .`when`()
            .post(baseUrl)
            .then()
            .statusCode(HttpStatus.SC_OK)
            .extract().body()

        Assertions.assertEquals( true, response.asString().contains("Total notifications sent - 2"),)
    }

}