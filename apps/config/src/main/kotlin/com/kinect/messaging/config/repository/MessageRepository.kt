package com.kinect.messaging.config.repository

import com.kinect.messaging.config.model.MessageEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository: MongoRepository<MessageEntity, String> {

}