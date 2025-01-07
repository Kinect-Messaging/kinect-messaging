package com.kinectmessaging.ch.service

import com.kinectmessaging.ch.model.ContactHistoryEntity
import com.kinectmessaging.ch.model.toEntity
import com.kinectmessaging.libs.common.ErrorConstants
import com.kinectmessaging.libs.exception.InvalidInputException
import com.kinectmessaging.libs.model.ContactMessages
import com.kinectmessaging.libs.model.DeliveryStatus
import com.kinectmessaging.libs.model.EngagementStatus
import com.kinectmessaging.libs.model.KContactHistory
import io.quarkus.panache.common.Sort
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class ContactHistoryService {

    fun saveContactHistory(contactHistory: KContactHistory): String {
        val chEntity = contactHistory.toEntity().persistOrUpdate()
        return "Contact History ${contactHistory.id} updated successfully"
    }

    fun updateContactMessageByMessageId(contactMessage: ContactMessages) {
        val existingContactHistoryEntity = ContactHistoryEntity.findByMessageId(contactMessage.messageId)
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
            updatedContactHistoryEntity.persistOrUpdate()
        } ?: throw InvalidInputException("No Contact History record for contact message id - ${contactMessage.messageId}")

    }

    fun updateContactMessageByDeliveryTrackingId(deliveryTrackingId: String, deliveryStatus: DeliveryStatus?, engagementStatus: EngagementStatus?) {
        val existingContactHistoryEntity = ContactHistoryEntity.findByDeliveryTrackingId(deliveryTrackingId)
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
            updatedContactHistoryEntity.persistOrUpdate()
        } ?: throw InvalidInputException("No Contact History record for delivery tracking id - $deliveryTrackingId")

    }

    fun findContactHistoryById(id: String): KContactHistory{
        val result = ContactHistoryEntity.findById(id)?.toResponse()
            ?: throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, contact-history-id : $id")
        return result
    }

    fun findAllContactHistory(pageNo: Int, pageSize: Int, sortBy: String, sortOrder: Sort.Direction): List<KContactHistory>?{
        val contactHistoryQuery = ContactHistoryEntity.findAll(Sort.by(sortBy, sortOrder)).page(pageNo, pageSize)
        val numberOfPages = contactHistoryQuery.pageCount()
        val count = contactHistoryQuery.count()
        if (count.toInt() == 0){
            throw InvalidInputException("${ErrorConstants.NO_DATA_FOUND_MESSAGE}, page-number - $pageNo, page-size - $pageSize, sort-by - $sortBy")
        }
        val dbResult = contactHistoryQuery.list()
        val result = mutableListOf<KContactHistory>()
        dbResult.forEach {
            result.add(it.toResponse())
        }
        return result
    }

}
