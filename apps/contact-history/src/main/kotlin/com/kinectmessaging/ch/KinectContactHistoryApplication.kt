package com.kinectmessaging.ch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.integration.config.EnableIntegration

@SpringBootApplication
@EnableMongoRepositories("com.kinectmessaging.ch.repository")
@EnableIntegration
class KinectContactHistoryApplication

fun main(args: Array<String>) {
	runApplication<KinectContactHistoryApplication>(*args)
}
