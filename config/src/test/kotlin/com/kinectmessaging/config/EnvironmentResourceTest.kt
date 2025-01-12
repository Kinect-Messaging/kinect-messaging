package com.kinectmessaging.config

import com.kinectmessaging.config.model.EnvironmentEntity
import com.kinectmessaging.config.model.MessageEntity
import com.kinectmessaging.libs.model.ChangeLog
import com.kinectmessaging.libs.model.EnvConfig
import com.kinectmessaging.libs.model.EnvNames
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
class EnvironmentResourceTest {
 private final val baseUrl = "/kinect/messaging/config/env"

 val envConfigs = mutableListOf(
  EnvConfig(
   envId = "1",
   envName = listOf(EnvNames.DEV),
   journeyId = "journey-1",
   messageId = "message-1",
   eventName = "CustomerCreated",
   changeLog = mutableListOf(ChangeLog(
    user = "Tester",
    time = LocalDateTime.now().toString(),
    comment = "Deployed to Dev"
   ))
  ),
  EnvConfig(
   envId = "2",
   envName = listOf(EnvNames.PROD),
   journeyId = "journey-1",
   messageId = "message-1",
   eventName = "CustomerWelcome",
   changeLog = mutableListOf(ChangeLog(
    user = "Tester",
    time = LocalDateTime.now().toString(),
    comment = "Deployed to Prod"
   ))
  )
 )

  @Test
 fun createEnvironment() {
   val requestInput = Json.encodeToString(envConfigs[0])
   given()
    .header("Content-Type", ContentType.JSON)
    .body(requestInput)
    .`when`().post(baseUrl)
    .then()
    .assertThat()
    .statusCode(HttpStatus.SC_OK)
   Assertions.assertEquals(1, EnvironmentEntity.count("_id", envConfigs[0].envId))
  }

@Test
 fun getEnvironmentById() {

 given()
  .`when`().get("$baseUrl/${envConfigs[0].envId}")
  .then()
  .assertThat()
  .statusCode(HttpStatus.SC_OK)
  .body("envId",equalTo(envConfigs[0].envId))
 }

@Test
 fun getEnvironments() {

 // given
 val pageNo = 1
 val pageSize = 1
 val sortBy = "envName"
 given()
  .`when`().get("$baseUrl?_start=$pageNo&_end=$pageSize&_sort=$sortBy&_order=ASC")
  .then()
  .assertThat()
  .statusCode(HttpStatus.SC_OK)
 Assertions.assertEquals(1, MessageEntity.count("_id", envConfigs[0].envId))
 }
}