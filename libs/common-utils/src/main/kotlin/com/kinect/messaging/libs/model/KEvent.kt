package com.kinect.messaging.libs.model

import com.fasterxml.jackson.databind.JsonNode
import java.util.*

data class KEvent(
    val eventId: String,
    val eventName: String,
    val eventTime: Date,
    val payload: JsonNode?,
    val recipients: List<Person>?
)

data class Contact (
    val email: String?,
    val phone: String?,
    val address: Address?
)

data class Person(
    val firstName: String,
    val lastName: String?,
    val contacts: List<Contact>?,
    // Default preference is English. Priority is set to 1 as highest.
    val preferredLanguage: Map<TemplateLanguage, Int>? = mapOf(Pair(TemplateLanguage.EN, 1))
)

data class Address(
    val addressLine1: String,
    val addressLine2: String?,
    val city: String,
    val state: String,
    val postalCode: String,
    val country: String
)

