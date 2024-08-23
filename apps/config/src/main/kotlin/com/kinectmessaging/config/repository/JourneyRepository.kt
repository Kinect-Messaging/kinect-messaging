package com.kinectmessaging.config.repository

import com.kinectmessaging.config.model.JourneyEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface JourneyRepository: MongoRepository<JourneyEntity, String> {

    fun findAllByJourneySteps_EventName(eventName: String): List<JourneyEntity>?

}