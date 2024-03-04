package com.kinect.messaging.config.service

import com.kinect.messaging.config.model.MessageEntity
import com.kinect.messaging.config.repository.MessageRepository
import com.kinect.messaging.libs.common.ErrorConstants
import com.kinect.messaging.libs.exception.InvalidInputException
import com.kinect.messaging.libs.model.MessageConfig
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class MessageService {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var messageRepository: MessageRepository

    fun saveMessage(messageConfig: MessageConfig): MessageConfig {
        val messageEntity = messageConfig.toMessageEntity()
        val result = messageRepository.save(messageEntity)
        return result.toMessageConfig()
    }

    fun findMessageById(messageId: String): MessageConfig?{
        val result = messageRepository.findById(messageId)
            .getOrNull()?.toMessageConfig()
            ?: throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, message id : $messageId")
        return result
    }

    fun findMessages(pageNo: Int, pageSize: Int, sortBy: String, sortOrder: Sort.Direction): List<MessageConfig>?{
        val pageOption = PageRequest.of(pageNo, pageSize, sortOrder, sortBy)
        val pageResult = messageRepository.findAll(pageOption)
        var dbResult = pageResult.content
        val result = mutableListOf<MessageConfig>()
        if (pageResult.totalElements.toInt() == 0){
            throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, page-number - $pageNo, page-size - $pageSize, sort-by - $sortBy")
        }
        /*while (pageResult.hasNext()) {
            val nextPageable = pageResult.nextPageable()
            val page = messageRepository.findAll(nextPageable)
            dbResult = page.content
        }*/
        dbResult.forEach {
            result.add(it.toMessageConfig())
        }
        return result
    }

    fun MessageConfig.toMessageEntity() = MessageEntity(
        messageId = messageId,
        messageName = messageName,
        messageCondition = messageCondition,
        emailConfig = emailConfig,
        messageVersion = messageVersion,
        messageStatus = messageStatus,
        journeyId = journeyId,
        auditInfo = auditInfo
    )

    fun MessageEntity.toMessageConfig() = MessageConfig(
        messageId = messageId,
        messageName = messageName,
        messageCondition = messageCondition,
        emailConfig = emailConfig,
        messageVersion = messageVersion,
        messageStatus = messageStatus,
        journeyId = journeyId,
        auditInfo = auditInfo
    )
}