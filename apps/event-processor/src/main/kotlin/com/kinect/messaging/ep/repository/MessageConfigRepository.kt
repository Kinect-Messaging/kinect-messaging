package com.kinect.messaging.ep.repository

import com.azure.spring.data.cosmos.repository.ReactiveCosmosRepository
import com.kinect.messaging.libs.model.KConfig
import org.springframework.stereotype.Repository

@Repository
interface MessageConfigRepository: ReactiveCosmosRepository<KConfig, String> {
}