package com.kinectmessaging.ep.service

import com.kinectmessaging.libs.exception.InvalidInputException
import com.kinectmessaging.libs.model.KEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class EventProcessorService {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun processEvent(event: KEvent): String{
        val result = "No message created."

        // Throw error if both payload and recipients are empty
        if (event.payload?.isNull == true && event.recipients?.isEmpty() == true){
            throw InvalidInputException(message = "Payload and recipients are empty in the event. Fix event data and retry.")
        }

        // Get matching configurations for the event

        return result
    }
}