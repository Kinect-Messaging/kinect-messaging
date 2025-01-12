package com.kinectmessaging.config.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.kinectmessaging.libs.model.Audit
import com.kinectmessaging.libs.model.JourneyConfig
import com.kinectmessaging.libs.model.JourneySteps
import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.PanacheMongoCompanion
import io.quarkus.mongodb.panache.kotlin.PanacheMongoEntityBase
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@MongoEntity(collection = "journey-config")
data class JourneyEntity(
    @SerialName("_id")
    @field:JsonProperty("journeyId")
    val journeyId: String,
    @field:JsonProperty("journeyName")
    val journeyName: String,
    @field:JsonProperty("journeySteps")
    val journeySteps: List<JourneySteps>?,
    @field:JsonProperty("auditInfo")
    val auditInfo: Audit
): PanacheMongoEntityBase(){
    companion object: PanacheMongoCompanion<JourneyEntity> {
        fun findById(id: String) = find("_id", id).firstResult()
        fun findJourneysByEventName(eventName: String) = list("journeySteps.eventName", eventName)
    }
    fun toResponse() = JourneyConfig(
        journeyId = journeyId,
        journeyName = journeyName,
        journeySteps = journeySteps,
        auditInfo = auditInfo
    )
}

fun JourneyConfig.toEntity() = JourneyEntity(
    journeyId = journeyId,
    journeyName = journeyName,
    journeySteps = journeySteps,
    auditInfo = auditInfo
)