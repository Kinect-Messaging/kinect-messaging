package com.kinectmessaging.ch.service

import com.kinectmessaging.ch.model.AzureEmailDeliveryReport
import com.kinectmessaging.ch.model.ContactHistoryEntity
import com.kinectmessaging.ch.repository.ContactHistoryRepository
import com.kinectmessaging.libs.common.ErrorConstants
import com.kinectmessaging.libs.exception.InvalidInputException
import com.kinectmessaging.libs.model.KContactHistory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class ContactHistoryService {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var contactHistoryRepository: ContactHistoryRepository
    fun saveContactHistory(contactHistory: KContactHistory): String {
        val chEntity = contactHistory.toContactHistoryEntity()
        contactHistoryRepository.save(chEntity)
        return "Contact History ${contactHistory.id} updated successfully"
    }

    fun findContactHistoryById(id: String): KContactHistory{
        val result = contactHistoryRepository.findById(id)
            .getOrNull()?.toContactHistory()
            ?: throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, contact-history-id : $id")
        return result
    }

    fun findContactHistory(pageNo: Int, pageSize: Int, sortBy: String, sortOrder: Sort.Direction): List<KContactHistory>?{
        val pageOption = PageRequest.of(pageNo, pageSize, sortOrder, sortBy)
        val pageResult = contactHistoryRepository.findAll(pageOption)
        val dbResult = pageResult.content
        val result = mutableListOf<KContactHistory>()
        if (pageResult.isEmpty){
            throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, page-number - $pageNo, page-size - $pageSize, sort-by - $sortBy")
        }
        dbResult.forEach {
            result.add(it.toContactHistory())
        }
        return result
    }

    fun processAzureEmailEvents(azureEmailDeliveryReport: AzureEmailDeliveryReport): KContactHistory {
        log.debug("Updating Email Status from Azure : {}", azureEmailDeliveryReport)
        val existingData = findContactHistoryById(azureEmailDeliveryReport.data.messageId)

        return existingData
    }

    fun KContactHistory.toContactHistoryEntity() = ContactHistoryEntity(
        id = id,
        journeyTransactionId = journeyTransactionId,
        journeyName = journeyName,
        messages = messages
    )

    fun ContactHistoryEntity.toContactHistory() = KContactHistory(
        id = id,
        journeyTransactionId = journeyTransactionId,
        journeyName = journeyName,
        messages = messages
    )

}
