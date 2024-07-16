package com.kinectmessaging.config.controller

import com.kinectmessaging.libs.exception.ErrorMessage
import com.kinectmessaging.libs.exception.InvalidInputException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ControllerExceptionHandler {
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

        return message
    }
}