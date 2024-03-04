package com.kinect.messaging.config.service

import com.kinect.messaging.config.model.EnvEntity
import com.kinect.messaging.config.repository.EnvRepository
import com.kinect.messaging.libs.common.ErrorConstants
import com.kinect.messaging.libs.exception.InvalidInputException
import com.kinect.messaging.libs.model.EnvConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class EnvService {

    @Autowired
    lateinit var envRepository: EnvRepository

    fun saveEnv(envConfig: EnvConfig): EnvConfig {
        val result = envRepository.save(envConfig.toEnvEntity())
        return result.toEnvConfig()
    }

    fun findEnvById(envId: String): EnvConfig?{
        val result = envRepository.findById(envId)
            .getOrNull()?.toEnvConfig()
            ?: throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}env id : $envId")
        return result
    }

    fun findEnvs(pageNo: Int, pageSize: Int, sortBy: String, sortOrder: Sort.Direction): List<EnvConfig>?{
        val pageOption = PageRequest.of(pageNo, pageSize, sortOrder, sortBy)
        val pageResult = envRepository.findAll(pageOption)
        var dbResult = pageResult.content
        val result = mutableListOf<EnvConfig>()
        if (pageResult.isEmpty){
            throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}page-number - $pageNo, page-size - $pageSize, sort-by - $sortBy")
        }
        /*while (pageResult.hasNext()) {
            val nextPageable = pageResult.nextPageable()
            val page = envRepository.findAll(nextPageable)
            dbResult = page.content
        }*/
        dbResult.forEach {
            result.add(it.toEnvConfig())
        }
        return result
    }

    fun EnvConfig.toEnvEntity() = EnvEntity(
        envId = envId,
        envName = envName,
        messageId = messageId,
        journeyId = journeyId,
        eventName = eventName,
        changeLog = changeLog
    )

    fun EnvEntity.toEnvConfig() = EnvConfig(
        envId = envId,
        envName = envName,
        messageId = messageId,
        journeyId = journeyId,
        eventName = eventName,
        changeLog = changeLog
    )
}