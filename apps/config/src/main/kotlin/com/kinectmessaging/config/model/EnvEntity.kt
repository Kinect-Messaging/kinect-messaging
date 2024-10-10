package com.kinectmessaging.config.model

import com.kinectmessaging.libs.model.ChangeLog
import com.kinectmessaging.libs.model.EnvNames
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "env-config")
data class EnvEntity(
    @Id
    val envId: String,
    @Indexed
    val envName: EnvNames,
    val journeyId: String,
    val messageId: String,
    val eventName: String,
    val changeLog: List<ChangeLog>
)
