package com.kinectmessaging.config.service


import com.kinectmessaging.config.model.MessageEntity
import com.kinectmessaging.config.model.toEntity

import com.kinectmessaging.libs.common.ErrorConstants
import com.kinectmessaging.libs.exception.InvalidInputException

import com.kinectmessaging.libs.model.MessageConfig
import io.quarkus.panache.common.Sort
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class MessageService {

    fun saveMessage(messageConfig: MessageConfig): MessageConfig {
        val messageEntity = messageConfig.toEntity()
        val result = messageEntity.persistOrUpdate()
        return messageConfig
    }

    fun findMessageById(messageId: String): MessageConfig?{
        val result = MessageEntity.findById(messageId)?.toResponse()
            ?: throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, message id : $messageId")
        return result
    }

    fun findAllMessages(pageNo: Int, pageSize: Int, sortBy: String, sortOrder: Sort.Direction): List<MessageConfig>?{
        val messageQuery = MessageEntity.findAll(Sort.by(sortBy, sortOrder)).page(pageNo, pageSize)
        val numberOfPages = messageQuery.pageCount()
        val count = messageQuery.count()
        if (count.toInt() == 0){
            throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, page-number - $pageNo, page-size - $pageSize, sort-by - $sortBy")
        }
        val dbResult = messageQuery.list()
        val result = mutableListOf<MessageConfig>()
        dbResult.forEach {
            result.add(it.toResponse())
        }
        return result
    }
}