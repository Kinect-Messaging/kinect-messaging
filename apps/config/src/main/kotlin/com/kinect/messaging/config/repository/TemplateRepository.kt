package com.kinect.messaging.config.repository

import com.kinect.messaging.config.model.TemplateEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TemplateRepository : MongoRepository<TemplateEntity, String> {

}