package com.kinectmessaging.ch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories


@SpringBootApplication
@EnableMongoRepositories("com.kinectmessaging.ch.repository")
class KinectContactHistoryApplication

fun main(args: Array<String>) {
//	EventFormatProvider.getInstance().registerFormat(JsonFormat())
	runApplication<KinectContactHistoryApplication>(*args)
}
