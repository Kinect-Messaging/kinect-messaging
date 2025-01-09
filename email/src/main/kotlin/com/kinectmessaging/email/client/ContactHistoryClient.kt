package com.kinectmessaging.email.com.kinectmessaging.email.client

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import io.cloudevents.CloudEvent
import io.cloudevents.jackson.JsonFormat
import io.quarkus.rest.client.reactive.NotBody
import io.quarkus.rest.client.reactive.Url
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.HeaderParam
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@RegisterRestClient(configKey = "contact-history-api")
interface ContactHistoryClient {
    @POST
    @Consumes(JsonFormat.CONTENT_TYPE)
    @ClientHeaderParam(name = "aeg-sas-key", value = ["\${app.client.contact-history.access-key}"])
    fun updateContactMessages(
        @Url url: String?,
        event: @Serializable(with = CloudEventSerializer::class) CloudEvent
    )
}

class CloudEventSerializer : KSerializer<CloudEvent> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("CloudEvent", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: CloudEvent) {
        val mapper = ObjectMapper()
        val cloudEventString = mapper.writeValueAsString(value)
        encoder.encodeString(cloudEventString)
    }

    override fun deserialize(decoder: Decoder): CloudEvent {
        val mapper = ObjectMapper()
        val factory: JsonFactory = mapper.factory
        val parser: JsonParser = factory.createParser(decoder.decodeString())
        return mapper.readValue(parser, CloudEvent::class.java)
    }

}