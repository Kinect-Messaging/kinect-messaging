package com.kinect.messaging.config.service

import com.azure.spring.data.cosmos.core.query.CosmosPageRequest
import com.kinect.messaging.config.model.JourneyEntity
import com.kinect.messaging.config.repository.JourneyRepository
import com.kinect.messaging.libs.common.ErrorConstants
import com.kinect.messaging.libs.exception.InvalidInputException
import com.kinect.messaging.libs.model.JourneyConfig
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class JourneyService {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var journeyRepository: JourneyRepository

    fun saveJourney(journeyConfig: JourneyConfig): JourneyConfig {
        val journeyEntity = journeyConfig.toJourneyEntity()
        val result = journeyRepository.save(journeyEntity)
        return result.toJourneyConfig()
    }

    fun findJourneyById(journeyId: String): JourneyConfig?{
        val result = journeyRepository.findById(journeyId)
            .getOrNull()?.toJourneyConfig()
            ?: throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, journey id : $journeyId")
        return result
    }

    fun findJourneys(pageNo: Int = 0, pageSize: Int = 20, sortBy: String = "journeyId"): List<JourneyConfig>?{
        val pageOption = CosmosPageRequest(pageNo, pageSize, null, Sort.by(sortBy))
        val pageResult = journeyRepository.findAll(pageOption)
        var dbResult = pageResult.content
        val result = mutableListOf<JourneyConfig>()
        if (pageResult.isEmpty){
            throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, page-number - $pageNo, page-size - $pageSize, sort-by - $sortBy")
        }
        while (pageResult.hasNext()) {
            val nextPageable = pageResult.nextPageable()
            val page = journeyRepository.findAll(nextPageable)
            dbResult = page.content
        }
        dbResult.forEach {
            result.add(it.toJourneyConfig())
        }
        return result
    }

    fun JourneyConfig.toJourneyEntity() = JourneyEntity(
        journeyId = journeyId,
        journeyName = journeyName,
        journeySteps = journeySteps,
        auditInfo = auditInfo
    )

    fun JourneyEntity.toJourneyConfig() = JourneyConfig(
        journeyId = journeyId,
        journeyName = journeyName,
        journeySteps = journeySteps,
        auditInfo = auditInfo
    )
}