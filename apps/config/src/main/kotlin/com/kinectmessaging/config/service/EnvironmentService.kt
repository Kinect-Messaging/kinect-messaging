package com.kinectmessaging.config.service

import com.kinectmessaging.config.model.EnvEntity
import com.kinectmessaging.config.repository.EnvRepository
import com.kinectmessaging.libs.common.ErrorConstants
import com.kinectmessaging.libs.exception.InvalidInputException
import com.kinectmessaging.libs.model.EnvConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class EnvironmentService {

    @Autowired
    lateinit var envRepository: EnvRepository

    fun saveEnvironment(envConfig: EnvConfig): EnvConfig {
        val result = envRepository.save(envConfig.toEnvEntity())
        return result.toEnvConfig()
    }

    fun publishEnvironments(envConfigs: List<EnvConfig>): String {
        val result = "Failed"
        envConfigs.forEach { envConfig ->
            envConfig.envName
        }
        return result
    }

    fun findEnvironmentById(envId: String): EnvConfig?{
        val result = envRepository.findById(envId)
            .getOrNull()?.toEnvConfig()
            ?: throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}env id : $envId")
        return result
    }

    fun findEnvironments(pageNo: Int, pageSize: Int, sortBy: String, sortOrder: Sort.Direction): List<EnvConfig>?{
        val pageOption = PageRequest.of(pageNo, pageSize, sortOrder, sortBy)
        val pageResult = envRepository.findAll(pageOption)
        val dbResult = pageResult.content
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