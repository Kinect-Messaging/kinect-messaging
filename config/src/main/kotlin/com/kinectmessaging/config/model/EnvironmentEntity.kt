package com.kinectmessaging.config.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.kinectmessaging.libs.model.ChangeLog
import com.kinectmessaging.libs.model.EnvConfig
import com.kinectmessaging.libs.model.EnvNames
import io.quarkus.mongodb.panache.common.MongoEntity
import io.quarkus.mongodb.panache.kotlin.PanacheMongoCompanion
import io.quarkus.mongodb.panache.kotlin.PanacheMongoEntityBase
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@MongoEntity(collection = "env-config")
data class EnvironmentEntity(
    @SerialName("_id")
    @field:JsonProperty("envId")
    val envId: String,
    @field:JsonProperty("envName")
    val envName: List<EnvNames>,
    @field:JsonProperty("journeyId")
    val journeyId: String,
    @field:JsonProperty("messageId")
    val messageId: String,
    @field:JsonProperty("eventName")
    val eventName: String,
    @field:JsonProperty("changeLog")
    val changeLog: List<ChangeLog>
): PanacheMongoEntityBase(){
    companion object: PanacheMongoCompanion<EnvironmentEntity> {
        fun findById(id: String) = EnvironmentEntity.find("_id", id).firstResult()
    }
    fun toResponse() = EnvConfig(
        envId = envId,
        envName = envName,
        messageId = messageId,
        journeyId = journeyId,
        eventName = eventName,
        changeLog = changeLog
    )
}

fun EnvConfig.toEntity() = EnvironmentEntity(
    envId = envId,
    envName = envName,
    messageId = messageId,
    journeyId = journeyId,
    eventName = eventName,
    changeLog = changeLog
)
