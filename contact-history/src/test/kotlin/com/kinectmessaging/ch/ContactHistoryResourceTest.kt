package com.kinectmessaging.ch

import com.kinectmessaging.ch.model.ContactHistoryEntity
import com.kinectmessaging.libs.model.*
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.http.HttpStatus
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@QuarkusTest
class ContactHistoryResourceTest {
    private final val baseUrl = "/kinect/messaging/contact-history"

    val contactHistoryData = mutableListOf(
        KContactHistory(
            id = "1",
            journeyTransactionId = "journey-1",
            journeyName = "Welcome Customer Journey",
            sourceEventId = "source-1",
            messages = ContactMessages(
                contactAddress = "test@xyz.com",
                messageId = "message-1",
                deliveryTrackingId = "delivery-1",
                deliveryChannel = DeliveryChannel.EMAIL,
                deliveryStatus = mutableListOf(DeliveryStatus(
                    status = HistoryStatusCodes.SENT,
                    statusTime = LocalDateTime.now(),
                    statusMessage = "Success",
                    originalStatus = null
                )),
                engagementStatus = null,
            ),
        ),
        KContactHistory(
            id = "2",
            journeyTransactionId = "journey-2",
            journeyName = "Welcome Customer Journey",
            sourceEventId = "source-2",
            messages = ContactMessages(
                contactAddress = "test-2@xyz.com",
                messageId = "message-2",
                deliveryTrackingId = "delivery-2",
                deliveryChannel = DeliveryChannel.EMAIL,
                deliveryStatus = mutableListOf(DeliveryStatus(
                    status = HistoryStatusCodes.DELIVERED,
                    statusTime = LocalDateTime.now(),
                    statusMessage = "Success",
                    originalStatus = null
                )),
                engagementStatus = null,
            ),
        )
    )

    @Test
    fun createContactHistory() {
        val requestInput = Json.encodeToString(contactHistoryData[0])
        given()
            .header("Content-Type", ContentType.JSON)
            .body(requestInput)
            .`when`().post(baseUrl)
            .then()
            .assertThat()
            .statusCode(HttpStatus.SC_NO_CONTENT)
        Assertions.assertEquals(1, ContactHistoryEntity.count("_id", contactHistoryData[0].id))
    }

    @Test
    fun getContactHistoryById() {
        given()
            .`when`().get("$baseUrl/${contactHistoryData[0].id}")
            .then()
            .assertThat()
            .statusCode(HttpStatus.SC_OK)
            .body("id", equalTo(contactHistoryData[0].id))
    }

    @Test
    fun getAllContactHistory() {

        // given
        val pageNo = 1
        val pageSize = 1
        val sortBy = "journeyName"
        given()
            .`when`().get("$baseUrl?_start=$pageNo&_end=$pageSize&_sort=$sortBy&_order=ASC")
            .then()
            .assertThat()
            .statusCode(HttpStatus.SC_OK)

        Assertions.assertEquals(1, ContactHistoryEntity.count("_id", contactHistoryData[0].id))
    }
}