package com.kinect.messaging.template.repository

import com.azure.spring.data.cosmos.repository.CosmosRepository
import com.kinect.messaging.template.model.TemplateEntity
import org.springframework.stereotype.Repository

@Repository
interface TemplateRepository : CosmosRepository<TemplateEntity, String> {
}