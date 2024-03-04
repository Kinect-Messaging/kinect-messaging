package com.kinect.messaging.template

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
class KinectTemplateApplication

fun main(args: Array<String>) {
	runApplication<KinectTemplateApplication>(*args)
}
