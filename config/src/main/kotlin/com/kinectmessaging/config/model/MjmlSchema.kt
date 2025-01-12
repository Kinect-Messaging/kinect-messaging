package com.kinectmessaging.config.model

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class MjmlRequest(
    val mjml: String,
    val options: String? = "{ beautify: true, keepComments: false }"
)

@Serializable
data class MjmlResponse(
    val html: String,
    val json: String? = null,
    val errors: List<String>? = null
)

data class MjmlError(
    val requestId: String,
    val startedAt: LocalDateTime,
    val message: String
)
