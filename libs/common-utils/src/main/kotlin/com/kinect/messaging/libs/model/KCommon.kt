package com.kinect.messaging.libs.model

data class Audit(
    val createdBy: String,
    val createdTime: String,
    val updatedBy: String?,
    val updatedTime: String?
)

data class ChangeLog(
    val user: String,
    val time: String,
    val comment: String
)
