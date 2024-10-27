package com.kinectmessaging.ch.repository

import com.kinectmessaging.ch.model.ContactHistoryEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ContactHistoryRepository: MongoRepository<ContactHistoryEntity, String> {
    fun findByMessages_MessageId(eventName: String): ContactHistoryEntity?
    fun findByMessages_DeliveryTrackingId(eventName: String): ContactHistoryEntity?
}