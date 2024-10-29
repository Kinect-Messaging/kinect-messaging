package com.kinectmessaging.ch.config

//import com.azure.spring.integration.storage.queue.inbound.StorageQueueMessageSource
//import com.azure.spring.messaging.storage.queue.core.StorageQueueTemplate
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.integration.annotation.InboundChannelAdapter
//import org.springframework.integration.annotation.Poller
//
//@Configuration
//class AzureSQConfiguration {
//
//    @Value("\${spring.cloud.azure.storage.queue.queue-name}")
//    var storageQueueName = "dev-kinect-email-events"
//
//    @Bean
//    @InboundChannelAdapter(channel = INPUT_CHANNEL, poller = Poller(fixedDelay = "1000"))
//    fun storageQueueMessageSource(storageQueueTemplate: StorageQueueTemplate?): StorageQueueMessageSource {
//        return StorageQueueMessageSource(storageQueueName, storageQueueTemplate)
//    }
//
//    companion object {
//        private val LOGGER: Logger = LoggerFactory.getLogger(this::class.java)
//        private const val INPUT_CHANNEL = "input"
//    }
//}