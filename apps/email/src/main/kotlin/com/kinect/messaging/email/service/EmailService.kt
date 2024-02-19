package com.kinect.messaging.email.service

import com.kinect.messaging.libs.model.KMessage

interface EmailService {

    suspend fun deliverEmail(kMessage: KMessage): String?
}