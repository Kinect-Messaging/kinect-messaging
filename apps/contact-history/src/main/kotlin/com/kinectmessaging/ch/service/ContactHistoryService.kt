package com.kinectmessaging.ch.service

import com.kinectmessaging.ch.model.ContactHistoryEntity
import com.kinectmessaging.ch.repository.ContactHistoryRepository
import com.kinectmessaging.libs.common.ErrorConstants
import com.kinectmessaging.libs.exception.InvalidInputException
import com.kinectmessaging.libs.model.ContactMessages
import com.kinectmessaging.libs.model.DeliveryStatus
import com.kinectmessaging.libs.model.EngagementStatus
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


    fun updateContactMessageByMessageId(contactMessage: ContactMessages) {
        val existingContactHistoryEntity = contactHistoryRepository.findByMessages_MessageId(contactMessage.messageId)
        existingContactHistoryEntity?.messages?.let { currentMessage ->
            val deliveryStatuses = mutableListOf<DeliveryStatus>()
            deliveryStatuses.addAll(currentMessage.deliveryStatus)
            if (deliveryStatuses.firstOrNull { it.status ==  contactMessage.deliveryStatus.firstOrNull()?.status} == null){
                deliveryStatuses.addAll(contactMessage.deliveryStatus)
            }

            val engagementStatuses = mutableListOf<EngagementStatus>()
            currentMessage.engagementStatus?.let { engagementStatuses.addAll(it) }
            contactMessage.engagementStatus?.let { newStatus ->
                if (engagementStatuses.firstOrNull { it.engagementType ==  newStatus.firstOrNull()?.engagementType} == null){
                    engagementStatuses.addAll(newStatus)
                }
            }


            val updatedContactMessage = currentMessage.copy(
                deliveryTrackingId = contactMessage.deliveryTrackingId,
                deliveryStatus = deliveryStatuses,
                engagementStatus = engagementStatuses
            )
            val updatedContactHistoryEntity = existingContactHistoryEntity.copy(
                messages = updatedContactMessage
            )
            contactHistoryRepository.save(updatedContactHistoryEntity)
        } ?: throw InvalidInputException("No Contact History record for contact message id - ${contactMessage.messageId}")

    }

    fun updateContactMessageByDeliveryTrackingId(deliveryTrackingId: String, deliveryStatus: DeliveryStatus?, engagementStatus: EngagementStatus?) {
        val existingContactHistoryEntity = contactHistoryRepository.findByMessages_DeliveryTrackingId(deliveryTrackingId)
        existingContactHistoryEntity?.messages?.let { currentMessage ->
            val deliveryStatuses = mutableListOf<DeliveryStatus>()
            deliveryStatuses.addAll(currentMessage.deliveryStatus)
            deliveryStatus?.let { newStatus ->
                if (deliveryStatuses.firstOrNull { it.status ==  newStatus.status} == null){
                    deliveryStatuses.add(newStatus)
                }
            }

            val engagementStatuses = mutableListOf<EngagementStatus>()
            currentMessage.engagementStatus?.let { engagementStatuses.addAll(it) }
            engagementStatus?.let { newStatus ->
                if (engagementStatuses.firstOrNull { it.engagementType ==  newStatus.engagementType} == null) {
                    engagementStatuses.add(newStatus)
                }
            }

            val updatedContactMessage = currentMessage.copy(
                deliveryTrackingId = deliveryTrackingId,
                deliveryStatus = deliveryStatuses,
                engagementStatus = engagementStatuses
            )
            val updatedContactHistoryEntity = existingContactHistoryEntity.copy(
                messages = updatedContactMessage
            )
            contactHistoryRepository.save(updatedContactHistoryEntity)
        } ?: throw InvalidInputException("No Contact History record for delivery tracking id - $deliveryTrackingId")

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

    private fun KContactHistory.toContactHistoryEntity() = ContactHistoryEntity(
        id = id,
        sourceEventId = sourceEventId,
        journeyTransactionId = journeyTransactionId,
        journeyName = journeyName,
        messages = messages
    )

    private fun ContactHistoryEntity.toContactHistory() = KContactHistory(
        id = id,
        sourceEventId = sourceEventId,
        journeyTransactionId = journeyTransactionId,
        journeyName = journeyName,
        messages = messages
    )


}
