package com.kinect.messaging.config.repository

import com.kinect.messaging.config.model.EnvEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface EnvRepository: MongoRepository<EnvEntity, String> {

}