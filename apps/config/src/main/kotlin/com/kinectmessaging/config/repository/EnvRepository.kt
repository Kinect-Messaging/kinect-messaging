package com.kinectmessaging.config.repository

import com.kinectmessaging.config.model.EnvEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface EnvRepository: MongoRepository<EnvEntity, String> {

}