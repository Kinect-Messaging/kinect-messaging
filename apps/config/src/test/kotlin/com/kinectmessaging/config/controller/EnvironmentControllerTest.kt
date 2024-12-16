package com.kinectmessaging.config.controller

import com.kinectmessaging.config.model.EnvEntity
import com.kinectmessaging.config.repository.EnvRepository
import com.kinectmessaging.libs.model.ChangeLog
import com.kinectmessaging.libs.model.EnvConfig
import com.kinectmessaging.libs.model.EnvNames
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDateTime
import java.util.*

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EnvironmentControllerTest {

    private final val baseUrl = "/kinect/messaging/config/env"

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockitoBean
    lateinit var envRepository: EnvRepository

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

    val envEntity = mutableListOf(
        EnvEntity(
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
        EnvEntity(
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
    fun createEnv() {
        // given
        val givenInput = envConfigs[0]

        // mock the database response
        val mockData = envEntity[0]

        given(envRepository.save(mockData)).willReturn(mockData)

        //when API is invoked, then valid response is returned
        webTestClient.post()
            .uri(baseUrl)
            .bodyValue(givenInput)
            .header("X-Transaction-Id", UUID.randomUUID().toString())
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("\$.envName").isEqualTo(EnvNames.DEV.toString())

    }

    @Test
    fun getEnvironmentById() {

        // given
        val givenInput = "1"

        // mock the database response
        val mockData = envEntity[0]
        given(envRepository.findById(givenInput)).willReturn(Optional.of(mockData))

        //when API is invoked, then valid response is returned
        webTestClient.get()
            .uri("$baseUrl/$givenInput")
            .header("X-Transaction-Id", UUID.randomUUID().toString())
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("\$.envName").isEqualTo(EnvNames.DEV.toString())

    }

    @Test
    fun getEnvironments() {

        // given
        val pageNo = 1
        val pageSize = 1
        val sortBy = "envName"
        val sortOrder = Sort.Direction.ASC

        // mock the database response
        val mockData = PageImpl(
            envEntity
        )
        given(envRepository.findAll(any(Pageable::class.java))).willReturn(mockData)

        // when API is invoked, then return valid response
        webTestClient.get()
            .uri("$baseUrl?_start=$pageNo&_end=$pageSize&_sort=$sortBy&_order=$sortOrder")
            .header("X-Transaction-Id", UUID.randomUUID().toString())
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("\$[0].envName").isEqualTo(EnvNames.DEV.toString())
            .jsonPath("\$[1].envName").isEqualTo(EnvNames.PROD.toString())

    }
}