package com.kinectmessaging.ch

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories


@SpringBootApplication
@EnableMongoRepositories("com.kinectmessaging.ch.repository")
class KinectContactHistoryApplication

fun main(args: Array<String>) {
//	val app = SpringApplication(
//		KinectContactHistoryApplication::class.java
//	)
//	app.applicationStartup = BufferingApplicationStartup(2048)
//	app.run(*args)
	runApplication<KinectContactHistoryApplication>(*args)
}
