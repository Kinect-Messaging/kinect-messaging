package com.kinectmessaging.config

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication
@EnableMongoRepositories("com.kinectmessaging.config.repository")
class KinectConfigApplication

fun main(args: Array<String>) {
	runApplication<KinectConfigApplication>(*args)
}
