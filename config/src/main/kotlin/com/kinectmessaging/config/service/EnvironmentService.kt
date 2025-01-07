package com.kinectmessaging.config.service

import com.kinectmessaging.config.model.EnvironmentEntity
import com.kinectmessaging.config.model.toEntity
import com.kinectmessaging.libs.common.ErrorConstants
import com.kinectmessaging.libs.exception.InvalidInputException
import com.kinectmessaging.libs.model.EnvConfig
import io.quarkus.panache.common.Sort
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class EnvironmentService {

    fun saveEnvironment(envConfig: EnvConfig): EnvConfig {
        val result = envConfig.toEntity().persistOrUpdate()
        return envConfig
    }

    fun publishEnvironments(envConfigs: List<EnvConfig>): String {
        val result = "Failed"
        envConfigs.forEach { envConfig ->
            envConfig.envName
        }
        return result
    }

    fun findEnvironmentById(envId: String): EnvConfig?{
        val result = EnvironmentEntity.findById(envId)?.toResponse()
            ?: throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}env id : $envId")
        return result
    }

    fun findAllEnvironments(pageNo: Int, pageSize: Int, sortBy: String, sortOrder: Sort.Direction): List<EnvConfig>?{
        val envQuery = EnvironmentEntity.findAll(Sort.by(sortBy, sortOrder)).page(pageNo, pageSize)
        val numberOfPages = envQuery.pageCount()
        val count = envQuery.count()
        if (count.toInt() == 0){
            throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, page-number - $pageNo, page-size - $pageSize, sort-by - $sortBy")
        }
        val dbResult = envQuery.list()
        val result = mutableListOf<EnvConfig>()
        dbResult.forEach {
            result.add(it.toResponse())
        }
        return result
    }
}