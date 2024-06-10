package com.kinect.messaging.ch.repository

import com.kinect.messaging.ch.model.ContactHistoryEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ContactHistoryRepository: MongoRepository<ContactHistoryEntity, String> {
}