package com.kinect.messaging.email.controller

import com.kinect.messaging.libs.model.DeliveryChannel
import com.kinect.messaging.libs.model.EmailData
import com.kinect.messaging.libs.model.KMessage
import com.kinect.messaging.libs.model.TargetSystem
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.util.HtmlUtils
import java.util.*
import javax.mail.internet.InternetAddress

@AutoConfigureMockMvc
@AutoConfigureWebTestClient(timeout = "36000")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmailControllerTest {


    private final val baseUrl = "/kinect/messaging/email"

    @Autowired
    lateinit var webTestClient: WebTestClient

    /**
     * Validate html email content for the provided id and personalization content. Does not work with actual email sending.
     * **** Turn off email sending by setting the property app.feature-flag.send-email=false ****
     */
    @Test
    fun `given Email Data with Azure when send Email then receive Email`() {
        val personalizationData = mutableMapOf(
            Pair("promotion", mutableMapOf(Pair("name", "New Year Promotion"))),
            Pair("users", mutableMapOf(Pair("receivingUser", "Jane"), Pair("sendingUser", "Doe")))
        )
        //given
        val givenInput = KMessage(
            id = UUID.randomUUID().toString(),
            sourceId = UUID.randomUUID().toString(),
            deliveryChannel = DeliveryChannel.EMAIL,
            targetSystem = TargetSystem.AZURE_COMMUNICATION_SERVICE,
            emailData = EmailData(
                textTemplateId = "a53782b1-fbd5-443e-a307-1808cde12462",
                htmlTemplateId = "\$663149e9-2fcb-4b16-94b8-a5697cbc56b9",
                subject = "Unit Test ${Math.random()}",
                toRecipients = mutableListOf(InternetAddress("rajp.work@gmail.com", "Tester")),
                senderAddress = "DoNotReply@d78f8366-7323-4a98-8626-6288771825fb.azurecomm.net",
                personalizationData = personalizationData
            )
        )

        //mock email response
        /*given(emailService.sendEmailWithAzure(org.mockito.kotlin.any()))
            .willAnswer{
            val argument = it.arguments[0]
            when(argument){
                is EmailMessage -> "Successfully sent email"
                else -> "Invalid data for email message"
            }
        }*/

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
//        assert(actualResult == "Successfully sent email")
        assert(actualResult?.equals(HtmlUtils.htmlEscape(actualResult)) == false) // checks for html content in response
        assert(actualResult?.contains("New Year Promotion") == true) // checks for personalization in response
    }
}