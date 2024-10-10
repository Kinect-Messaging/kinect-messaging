package com.kinectmessaging.config.model

import com.kinectmessaging.libs.model.Audit
import com.kinectmessaging.libs.model.JourneySteps
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "journey-config")
data class JourneyEntity(
    @Id
    val journeyId: String,
    @Indexed
    val journeyName: String,
    val journeySteps: List<JourneySteps>?,
    val auditInfo: Audit
)
