package com.kinectmessaging.config.repository

import com.kinectmessaging.config.model.MessageEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository: MongoRepository<MessageEntity, String> {

}