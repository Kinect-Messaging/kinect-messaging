package com.kinect.messaging.config.repository

import com.azure.spring.data.cosmos.repository.CosmosRepository
import com.kinect.messaging.config.model.MessageEntity
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository: CosmosRepository<MessageEntity, String> {

}