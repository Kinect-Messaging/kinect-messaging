package com.kinectmessaging.config.service

import com.kinectmessaging.config.model.JourneyEntity
import com.kinectmessaging.config.model.toEntity
import com.kinectmessaging.libs.common.ErrorConstants
import com.kinectmessaging.libs.exception.InvalidInputException
import com.kinectmessaging.libs.model.JourneyConfig
import io.quarkus.panache.common.Sort
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class JourneyService {

    fun saveJourney(journeyConfig: JourneyConfig): JourneyConfig {
        val journeyEntity = journeyConfig.toEntity()
        val result = journeyEntity.persistOrUpdate()
        return journeyConfig
    }

    fun findJourneyById(journeyId: String): JourneyConfig?{
        val result = JourneyEntity.findById(journeyId)?.toResponse()
            ?: throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, journey id : $journeyId")
        return result
    }

    fun findJourneysByEventName(eventName: String): List<JourneyConfig> {
        val result = mutableListOf<JourneyConfig>()
        val dbResult = JourneyEntity.findJourneysByEventName(eventName)
        dbResult.forEach {
            result.add(it.toResponse())
        }
        return result
    }

    fun findAllJourneys(pageNo: Int, pageSize: Int, sortBy: String, sortOrder: Sort.Direction): List<JourneyConfig>?{
        val journeyQuery = JourneyEntity.findAll(Sort.by(sortBy, sortOrder)).page(pageNo, pageSize)
        val numberOfPages = journeyQuery.pageCount()
        val count = journeyQuery.count()
        if (count.toInt() == 0){
            throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, page-number - $pageNo, page-size - $pageSize, sort-by - $sortBy")
        }
        val dbResult = journeyQuery.list()
        val result = mutableListOf<JourneyConfig>()
        dbResult.forEach {
            result.add(it.toResponse())
        }
        return result
    }

}