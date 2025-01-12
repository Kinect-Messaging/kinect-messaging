package com.kinectmessaging.config

import com.kinectmessaging.config.model.MessageEntity
import com.kinectmessaging.libs.model.*
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.apache.http.HttpStatus
import org.hamcrest.core.IsEqual.equalTo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

@QuarkusTest
class MessageResourceTest {
 private final val baseUrl = "/kinect/messaging/config/message"

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
   val requestInput = Json.encodeToString(messageConfig[0])
   given()
    .header("Content-Type", ContentType.JSON)
    .body(requestInput)
    .`when`().post(baseUrl)
    .then()
    .assertThat()
    .statusCode(HttpStatus.SC_OK)
   Assertions.assertEquals(1, MessageEntity.count("_id", messageConfig[0].messageId))
  }

@Test
 fun getMessageById() {

 given()
  .`when`().get("$baseUrl/${messageConfig[0].messageId}")
  .then()
  .assertThat()
  .statusCode(HttpStatus.SC_OK)
  .body("messageId",equalTo(messageConfig[0].messageId))
 }

@Test
 fun getMessages() {

 // given
 val pageNo = 1
 val pageSize = 1
 val sortBy = "messageName"
 given()
  .`when`().get("$baseUrl?_start=$pageNo&_end=$pageSize&_sort=$sortBy&_order=ASC")
  .then()
  .assertThat()
  .statusCode(HttpStatus.SC_OK)
 Assertions.assertEquals(1, MessageEntity.count("_id", messageConfig[0].messageId))
 }
}