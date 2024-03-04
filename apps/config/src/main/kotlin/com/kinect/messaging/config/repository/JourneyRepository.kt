package com.kinect.messaging.config.repository

import com.kinect.messaging.config.model.JourneyEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface JourneyRepository: MongoRepository<JourneyEntity, String> {

}