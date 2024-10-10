package com.kinectmessaging.config.repository

import com.kinectmessaging.config.model.TemplateEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TemplateRepository : MongoRepository<TemplateEntity, String> {

}