package com.kinect.messaging.config.controller

import com.kinect.messaging.config.model.TemplateEntity
import com.kinect.messaging.config.repository.TemplateRepository
import com.kinect.messaging.libs.model.Audit
import com.kinect.messaging.libs.model.TemplateLanguage
import com.kinect.messaging.libs.model.TemplatePersonalizationRequest
import com.kinect.messaging.libs.model.TemplateType
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.anyString
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@SpringBootTest
@AutoConfigureWebTestClient
class TemplateControllerTest {

    private final val baseUrl = "/kinect/messaging/template"

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockBean
    lateinit var templateRepository: TemplateRepository

    val templateData = mutableListOf(
        TemplateEntity(
            templateId = "1",
            templateName = "Test Get By Id 1",
            templateType = TemplateType.CONTROL,
            templateLanguage = TemplateLanguage.EN,
            templateContent = "SGksCiAgICBXZSBob3BlIHRoaXMgZW1haWwgZmluZCB5b3Ugd2VsbC4K",
            auditInfo = Audit(
                createdBy = "Tester",
                createdTime = Calendar.getInstance().time.toString(),
                updatedBy = "Tester",
                updatedTime = Calendar.getInstance().time.toString(),
            )
        ),
        TemplateEntity(
            templateId = "2",
            templateName = "Test Get By Id 2",
            templateType = TemplateType.CONTROL,
            templateLanguage = TemplateLanguage.EN,
            templateContent = "PG1qbWw+CiAgPG1qLWJvZHk+CiAgICA8IS0tIENvbXBhbnkgSGVhZGVyIC0tPgogICAgPG1qLXNlY3Rpb24gYmFja2dyb3VuZC1jb2xvcj0iI2YwZjBmMCI+CiAgICAgICAgPG1qLWNvbHVtbj4KICAgICAgICAgICAgPG1qLXRleHQgIGZvbnQtc3R5bGU9ImJvbGQiCiAgICAgICAgICAgICAgICBmb250LXNpemU9IjIwcHgiCiAgICAgICAgICAgICAgICBhbGlnbj0iY2VudGVyIgogICAgICAgICAgICAgICAgY29sb3I9IiM2MjYyNjIiPgogICAgICAgICAgICBDZW50cmFsIFBhcmsgQ3J1aXNlcwogICAgICAgICAgICA8L21qLXRleHQ+CiAgICAgICAgPC9tai1jb2x1bW4+CiAgICA8L21qLXNlY3Rpb24+CiAgICA8IS0tIEltYWdlIEhlYWRlciAtLT4KICAgIDxtai1zZWN0aW9uIGJhY2tncm91bmQtdXJsPSJodHRwczovL2NhLXRpbWVzLmJyaWdodHNwb3RjZG4uY29tL2RpbXM0L2RlZmF1bHQvMmFmMTY1Yy8yMTQ3NDgzNjQ3L3N0cmlwL3RydWUvY3JvcC8yMDQ4eDEzNjMrMCswL3Jlc2l6ZS8xNDQweDk1OCEvcXVhbGl0eS85MC8/dXJsPWh0dHBzJTNBJTJGJTJGd3d3LnRyYmltZy5jb20lMkZpbWctNGY1NjFkMzclMkZ0dXJiaW5lJTJGb3JsLWRpc25leWZhbnRhc3k3MjAxMjAzMDYwNjIwNTUiCiAgICAgICAgYmFja2dyb3VuZC1zaXplPSJjb3ZlciIKICAgICAgICBiYWNrZ3JvdW5kLXJlcGVhdD0ibm8tcmVwZWF0Ij4KICAgICAgICA8bWotY29sdW1uIHdpZHRoPSI2MDBweCI+CiAgICAgICAgICAgIDxtai10ZXh0ICBhbGlnbj0iY2VudGVyIgogICAgICAgICAgICAgICAgY29sb3I9IiNmZmYiCiAgICAgICAgICAgICAgICBmb250LXNpemU9IjQwcHgiCiAgICAgICAgICAgICAgICBmb250LWZhbWlseT0iSGVsdmV0aWNhIE5ldWUiPkNocmlzdG1hcyBEaXNjb3VudDwvbWotdGV4dD4KICAgICAgICAgICAgPG1qLWJ1dHRvbiBiYWNrZ3JvdW5kLWNvbG9yPSIjRjYzQTREIiBocmVmPSIjIj4KICAgICAgICAgICAgICAgIFNlZSBQcm9tb3Rpb25zCiAgICAgICAgICAgIDwvbWotYnV0dG9uPgogICAgICAgIDwvbWotY29sdW1uPgogICAgPC9tai1zZWN0aW9uPgogICAgPCEtLSBFbWFpbCBJbnRyb2R1Y3Rpb24gLS0+CiAgICA8bWotc2VjdGlvbiBiYWNrZ3JvdW5kLWNvbG9yPSIjZmFmYWZhIj4KICAgICAgICA8bWotY29sdW1uIHdpZHRoPSI0MDBweCI+CiAgICAgICAgICA8bWotdGV4dCBmb250LXN0eWxlPSJib2xkIgogICAgICAgICAgICBmb250LXNpemU9IjIwcHgiCiAgICAgICAgICAgIGZvbnQtZmFtaWx5PSJIZWx2ZXRpY2EgTmV1ZSIKICAgICAgICAgICAgY29sb3I9IiM2MjYyNjIiPlVsdGltYXRlIENocmlzdG1hcyBFeHBlcmllbmNlPC9tai10ZXh0PgogICAgICAgICAgICA8bWotdGV4dCBjb2xvcj0iIzUyNTI1MiI+CiAgICAgICAgICAgICAgICBMb3JlbSBpcHN1bSBkb2xvciBzaXQgYW1ldCwgY29uc2VjdGV0dXIgYWRpcGlzY2luZyBlbGl0LiBQcm9pbiBydXRydW0gZW5pbSBlZ2V0IG1hZ25hIGVmZmljaXR1ciwgZXUgc2VtcGVyIGF1Z3VlIHNlbXBlci4gQWxpcXVhbSBlcmF0IHZvbHV0cGF0LiBDcmFzIGlkIGR1aSBsZWN0dXMuIFZlc3RpYnVsdW0gc2VkIGZpbmlidXMgbGVjdHVzLCBzaXQgYW1ldCBzdXNjaXBpdCBuaWJoLiBQcm9pbiBuZWMgY29tbW9kbyBwdXJ1cy4gU2VkIGVnZXQgbnVsbGEgZWxpdC4gTnVsbGEgYWxpcXVldCBtb2xsaXMgZmF1Y2lidXMuCiAgICAgICAgICAgIDwvbWotdGV4dD4KICAgICAgICAgICAgPG1qLWJ1dHRvbiBiYWNrZ3JvdW5kLWNvbG9yPSIjRjQ1RTQzIiBocmVmPSIjIj5MZWFybiBtb3JlPC9tai1idXR0b24+CiAgICAgICAgPC9tai1jb2x1bW4+CiAgICA8L21qLXNlY3Rpb24+CiAgICA8IS0tIENvbHVtbnMgc2VjdGlvbiAtLT4KICAgIDxtai1zZWN0aW9uIGJhY2tncm91bmQtY29sb3I9IndoaXRlIj4KICAgICAgICA8IS0tIExlZnQgaW1hZ2UgLS0+CiAgICAgICAgPG1qLWNvbHVtbj4KICAgICAgICAgICAgPG1qLWltYWdlIHdpZHRoPSIyMDBweCIKICAgICAgICAgICAgICAgIHNyYz0iaHR0cHM6Ly9uYXZpcy1jb25zdWx0aW5nLmNvbS93cC1jb250ZW50L3VwbG9hZHMvMjAxOS8wOS9DcnVpc2UxLTEucG5nIi8+CiAgICAgICAgPC9tai1jb2x1bW4+CiAgICAgICAgPCEtLSByaWdodCBwYXJhZ3JhcGggLS0+CiAgICAgICAgPG1qLWNvbHVtbj4KICAgICAgICAgICAgPG1qLXRleHQgZm9udC1zdHlsZT0iYm9sZCIKICAgICAgICAgICAgICAgIGZvbnQtc2l6ZT0iMjBweCIKICAgICAgICAgICAgICAgIGZvbnQtZmFtaWx5PSJIZWx2ZXRpY2EgTmV1ZSIKICAgICAgICAgICAgICAgIGNvbG9yPSIjNjI2MjYyIj4KICAgICAgICAgICAgICAgIEFtYXppbmcgRXhwZXJpZW5jZXMKICAgICAgICAgICAgPC9tai10ZXh0PgogICAgICAgICAgICA8bWotdGV4dCBjb2xvcj0iIzUyNTI1MiI+CiAgICAgICAgICAgICAgICBMb3JlbSBpcHN1bSBkb2xvciBzaXQgYW1ldCwgY29uc2VjdGV0dXIgYWRpcGlzY2luZyBlbGl0LiAKICAgICAgICAgICAgICAgIFByb2luIHJ1dHJ1bSBlbmltIGVnZXQgbWFnbmEgZWZmaWNpdHVyLCBldSBzZW1wZXIgYXVndWUgc2VtcGVyLiAKICAgICAgICAgICAgICAgIEFsaXF1YW0gZXJhdCB2b2x1dHBhdC4gQ3JhcyBpZCBkdWkgbGVjdHVzLiBWZXN0aWJ1bHVtIHNlZCBmaW5pYnVzIAogICAgICAgICAgICAgICAgbGVjdHVzLgogICAgICAgICAgICA8L21qLXRleHQ+CiAgICAgICAgPC9tai1jb2x1bW4+CiAgICA8L21qLXNlY3Rpb24+CiAgICA8IS0tIEljb25zIC0tPgogICAgPG1qLXNlY3Rpb24gYmFja2dyb3VuZC1jb2xvcj0iI2ZiZmJmYiI+CiAgICAgICAgPG1qLWNvbHVtbj4KICAgICAgICAgICAgPG1qLWltYWdlIHdpZHRoPSIxMDBweCIgc3JjPSJodHRwczovLzE5MW4ubWouYW0vaW1nLzE5MW4vM3MveDBsLnBuZyIgLz4KICAgICAgICA8L21qLWNvbHVtbj4KICAgICAgICA8bWotY29sdW1uPgogICAgICAgICAgICA8bWotaW1hZ2Ugd2lkdGg9IjEwMHB4IiBzcmM9Imh0dHBzOi8vMTkxbi5tai5hbS9pbWcvMTkxbi8zcy94MDEucG5nIiAvPgogICAgICAgIDwvbWotY29sdW1uPgogICAgICAgIDxtai1jb2x1bW4+CiAgICAgICAgICAgIDxtai1pbWFnZSB3aWR0aD0iMTAwcHgiIHNyYz0iaHR0cHM6Ly8xOTFuLm1qLmFtL2ltZy8xOTFuLzNzL3gwcy5wbmciIC8+CiAgICAgICAgPC9tai1jb2x1bW4+CiAgICA8L21qLXNlY3Rpb24+CiAgICA8IS0tIFNvY2lhbCBpY29ucyAtLT4KICAgIDxtai1zZWN0aW9uIGJhY2tncm91bmQtY29sb3I9IiNlN2U3ZTciPgogICAgICAgIDxtai1jb2x1bW4+CiAgICAgICAgICAgIDxtai1zb2NpYWw+CiAgICAgICAgICAgICAgICA8bWotc29jaWFsLWVsZW1lbnQgbmFtZT0iaW5zdGFncmFtIiAvPgogICAgICAgICAgICA8L21qLXNvY2lhbD4KICAgICAgICA8L21qLWNvbHVtbj4KICAgIDwvbWotc2VjdGlvbj4KICA8L21qLWJvZHk+CjwvbWptbD4=",
            auditInfo = Audit(
                createdBy = "Tester",
                createdTime = Calendar.getInstance().time.toString(),
                updatedBy = "Tester",
                updatedTime = Calendar.getInstance().time.toString(),
            )
        ))

    @Test
    fun createTemplate() {

        //given
        val givenInput = templateData[0]
        val mockData = templateData[0]
        given(templateRepository.save(givenInput)).willReturn(mockData)

        //when, then
        webTestClient.post()
            .uri(baseUrl)
            .bodyValue(givenInput)
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("X-Transaction-Id", UUID.randomUUID().toString())
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("\$.templateName").isEqualTo("Test Get By Id 1")
    }

    @Test
    fun getTemplate() {
        //given
        val givenInput = "1"
        val expectedResult = templateData[0]
        given(templateRepository.findById(givenInput)).willReturn(Optional.of(expectedResult))

        //when, then
        webTestClient.get()
            .uri("$baseUrl/$givenInput")
            .header("X-Transaction-Id", UUID.randomUUID().toString())
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("\$.templateName").isEqualTo("Test Get By Id 1")
    }

    @Test
    fun getAllTemplates() {
        // given
        val pageNo = 1
        val pageSize = 1
        val sortBy = "templateName"
        val sortOrder = Sort.Direction.ASC

        // mock the database response
        val expectedResult = PageImpl(templateData)
        given(templateRepository.findAll(any(Pageable::class.java))).willReturn(expectedResult)
        webTestClient.get()
            .uri("$baseUrl?_start=$pageNo&_end=$pageSize&_sort=$sortBy&_order=ASC")
            .header("X-Transaction-Id", UUID.randomUUID().toString())
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("\$[0].templateName").isEqualTo("Test Get By Id 1")
            .jsonPath("\$[1].templateName").isEqualTo("Test Get By Id 2")

    }

    @Test
    fun `given personalization Request with no data then get HTML Template`(){

        //given
        val givenInput = TemplatePersonalizationRequest(
            "1",
            "2",
            null
        )
        val mockData = templateData


        given(templateRepository.findById(anyString()))
            .willAnswer {
                val argument = it.arguments[0]
                when(argument){
                    "1" -> Optional.of(mockData[0])
                    "2" -> Optional.of(mockData[1])
                    else -> Optional.empty()
                }

            }

        //when, then
        val actualResult = webTestClient.post()
            .uri("$baseUrl/personalize")
            .bodyValue(givenInput)
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("X-Transaction-Id", UUID.randomUUID().toString())
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("\$[0].templateName").isEqualTo("Test Get By Id 1")

    }

    @Test
    fun `given personalization Request with data then get HTML Template`(){

        //given
        val personalizationData = mutableMapOf(
            Pair("promotion", mutableMapOf(Pair("name", "New Year Promotion"))),
            Pair("users", mutableMapOf(Pair("receivingUser", "Jane"), Pair("sendingUser", "Doe")))
        )
        val givenInput = TemplatePersonalizationRequest(
            "1",
            "2",
            personalizationData
        )
        val mockData = templateData


        given(templateRepository.findById(anyString()))
            .willAnswer {
                val argument = it.arguments[0]
                when(argument){
                    "1" -> Optional.of(mockData[0])
                    "2" -> Optional.of(mockData[1])
                    else -> Optional.empty()
                }

            }

        //when, then
        val actualResult = webTestClient.post()
            .uri("$baseUrl/personalize")
            .bodyValue(givenInput)
            .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
            .header("X-Transaction-Id", UUID.randomUUID().toString())
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[1].templateContent").isNotEmpty
            .jsonPath("\$[1].templateContent").value<String> { it.contains("New Year Promotion") }

    }
}