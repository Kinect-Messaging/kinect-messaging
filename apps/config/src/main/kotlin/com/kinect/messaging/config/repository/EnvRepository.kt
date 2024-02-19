package com.kinect.messaging.config.repository

import com.azure.spring.data.cosmos.repository.CosmosRepository
import com.kinect.messaging.config.model.EnvEntity
import org.springframework.stereotype.Repository

@Repository
interface EnvRepository: CosmosRepository<EnvEntity, String> {

}