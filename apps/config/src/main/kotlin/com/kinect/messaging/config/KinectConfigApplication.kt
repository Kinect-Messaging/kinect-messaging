package com.kinect.messaging.config

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication
@EnableMongoRepositories("com.kinect.messaging.config.repository")
class KinectConfigApplication

fun main(args: Array<String>) {
	runApplication<KinectConfigApplication>(*args)
}
