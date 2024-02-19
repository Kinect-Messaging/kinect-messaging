package com.kinect.messaging.template.service

import com.azure.cosmos.CosmosException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler


// Cosmos related errors will get converted to 500 by spring (e.g. rate-limiting
// 429). this is
// handled explicitly
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
class CosmosExceptionHandler {
    @ExceptionHandler(CosmosException::class)
    fun handleCosmosException(e: CosmosException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.valueOf(e.statusCode)).body(e.message)
    }
}