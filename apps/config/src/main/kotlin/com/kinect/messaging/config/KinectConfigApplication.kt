package com.kinect.messaging.config

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KinectConfigApplication

fun main(args: Array<String>) {
	runApplication<KinectConfigApplication>(*args)
}
