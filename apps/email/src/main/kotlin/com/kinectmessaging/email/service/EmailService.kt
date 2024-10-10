package com.kinectmessaging.email.service

import com.kinectmessaging.libs.model.KMessage

interface EmailService {

    suspend fun deliverEmail(kMessage: KMessage): String?
}