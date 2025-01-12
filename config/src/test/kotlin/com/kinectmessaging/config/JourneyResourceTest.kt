package com.kinectmessaging.config

import com.kinectmessaging.config.model.JourneyEntity
import com.kinectmessaging.libs.model.Audit
import com.kinectmessaging.libs.model.JourneyConfig
import com.kinectmessaging.libs.model.JourneySteps
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
class JourneyResourceTest {
 private final val baseUrl = "/kinect/messaging/config/journey"
  private final val messageConfig = mutableMapOf(
   Pair("1", "Customer Created"),
   Pair("2", "Customer Welcome"))


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


 private val id = "1"
 private val testData = JourneyConfig(
  journeyId = id,
  journeyName = "Welcome Customer Journey",
  journeySteps = mutableListOf(
   JourneySteps(
    seqId = 1,
    eventName = "TestEvent",
    stepCondition = "customer.stats='new'",
    messageConfigs = messageConfig
   )
  ),
  auditInfo = auditInfo
 )

  @Test
 fun createJourney() {
   val requestInput = Json.encodeToString(testData)
   given()
    .header("Content-Type", ContentType.JSON)
    .body(requestInput)
    .`when`().post(baseUrl)
    .then()
    .assertThat()
    .statusCode(HttpStatus.SC_OK)
   Assertions.assertEquals(1, JourneyEntity.count("_id", testData.journeyId))
  }

@Test
 fun getJourneyById() {

 given()
  .`when`().get("$baseUrl/$id")
  .then()
  .assertThat()
  .statusCode(HttpStatus.SC_OK)
  .body("journeyId",equalTo(testData.journeyId))
 }

@Test
 fun getJourneyByEventName() {
 // given
 val eventName = "TestEvent"

 given()
  .`when`().get("$baseUrl/?event-name=$eventName")
  .then()
  .assertThat()
  .statusCode(HttpStatus.SC_OK)
  .body("[0].journeyName", equalTo(testData.journeyName))
 }

@Test
 fun getJourneys() {

 // given
 val pageNo = 1
 val pageSize = 1
 val sortBy = "journeyName"
 given()
  .`when`().get("$baseUrl?_start=$pageNo&_end=$pageSize&_sort=$sortBy&_order=ASC")
  .then()
  .assertThat()
  .statusCode(HttpStatus.SC_OK)

 Assertions.assertEquals(1, JourneyEntity.count("_id", testData.journeyId))

 }
}