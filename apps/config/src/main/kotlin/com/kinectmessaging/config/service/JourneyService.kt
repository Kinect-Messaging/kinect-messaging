package com.kinectmessaging.config.service

import com.kinectmessaging.config.model.JourneyEntity
import com.kinectmessaging.config.repository.JourneyRepository
import com.kinectmessaging.libs.common.ErrorConstants
import com.kinectmessaging.libs.exception.InvalidInputException
import com.kinectmessaging.libs.model.JourneyConfig
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
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

    fun findJourneys(pageNo: Int, pageSize: Int, sortBy: String, sortOrder: Sort.Direction): List<JourneyConfig>?{
        val pageOption = PageRequest.of(pageNo, pageSize, sortOrder, sortBy)
        val pageResult = journeyRepository.findAll(pageOption)
        if (pageResult.isEmpty){
            throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, page-number - $pageNo, page-size - $pageSize, sort-by - $sortBy")
        }
        var dbResult = pageResult.content
        val result = mutableListOf<JourneyConfig>()

        // no need to iterate all pages
        /*while (pageResult.hasNext()) {
            val nextPageable = pageResult.nextPageable()
            val page = journeyRepository.findAll(nextPageable)
            dbResult = page.content
        }*/
        dbResult.forEach {
            result.add(it.toJourneyConfig())
        }
        return result
    }

    fun findJourneyByEventName(eventName: String): List<JourneyConfig> {
        val result = mutableListOf<JourneyConfig>()
        val dbResult = journeyRepository.findAllByJourneySteps_EventName(eventName)
        dbResult?.forEach {
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