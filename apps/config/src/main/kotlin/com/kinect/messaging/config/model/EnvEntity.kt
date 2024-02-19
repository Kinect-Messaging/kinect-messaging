package com.kinect.messaging.config.model

import com.azure.spring.data.cosmos.core.mapping.Container
import com.azure.spring.data.cosmos.core.mapping.PartitionKey
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import com.kinect.messaging.libs.model.ChangeLog
import com.kinect.messaging.libs.model.EnvNames
import org.springframework.data.annotation.Id

@Container(containerName = "env-config")
data class EnvEntity(
    @Id
    val envId: String,
    @PartitionKey
    val envName: EnvNames,
    val journeyId: String,
    val messageId: String,
    val eventName: String,
    val changeLog: List<ChangeLog>
)
