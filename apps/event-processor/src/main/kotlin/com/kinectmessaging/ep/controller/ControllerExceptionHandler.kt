package com.kinectmessaging.ep.controller

import com.kinectmessaging.libs.common.ErrorConstants
import com.kinectmessaging.libs.exception.ErrorMessage
import com.kinectmessaging.libs.exception.InvalidInputException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ControllerExceptionHandler {
    private val log = LoggerFactory.getLogger(this::class.java)
    @ExceptionHandler(InvalidInputException::class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    fun invalidInputException(ex: InvalidInputException): ErrorMessage? {
        val message = ex.message?.let {
            ErrorMessage(
                it,
                HttpStatus.NOT_FOUND.value(),
                null
            )
        }
        log.error("${ErrorConstants.HTTP_5XX_ERROR_MESSAGE} ${ex.printStackTrace()}")
        return message
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    fun globalExceptionHandler(ex: Exception): ErrorMessage? {
        val message = ex.message?.let {
            ErrorMessage(
                it,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                null
            )
        }
        log.error("${ErrorConstants.HTTP_5XX_ERROR_MESSAGE} ${ex.printStackTrace()}")
        return message
    }
}