package com.kinectmessaging.config.model

import java.time.LocalDateTime

data class MjmlRequest(
    val mjml: String,
    val options: String? = "{ beautify: true, keepComments: false }"
)

data class MjmlResponse(
    val html: String,
    val json: String?,
    val errors: List<String>?
)

data class MjmlError(
    val requestId: String,
    val startedAt: LocalDateTime,
    val message: String
)
