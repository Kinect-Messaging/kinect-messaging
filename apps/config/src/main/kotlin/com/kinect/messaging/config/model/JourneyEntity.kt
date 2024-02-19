package com.kinect.messaging.config.model

import com.azure.spring.data.cosmos.core.mapping.Container
import com.azure.spring.data.cosmos.core.mapping.PartitionKey
import com.kinect.messaging.libs.model.Audit
import com.kinect.messaging.libs.model.JourneySteps
import org.springframework.data.annotation.Id

@Container(containerName = "journey-config")
data class JourneyEntity(
    @Id
    val journeyId: String,
    @PartitionKey
    val journeyName: String,
    val journeySteps: List<JourneySteps>?,
    val auditInfo: Audit
)
