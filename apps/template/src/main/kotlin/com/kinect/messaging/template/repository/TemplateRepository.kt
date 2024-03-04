package com.kinect.messaging.template.repository

import com.kinect.messaging.template.model.TemplateEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TemplateRepository : MongoRepository<TemplateEntity, String> {

}