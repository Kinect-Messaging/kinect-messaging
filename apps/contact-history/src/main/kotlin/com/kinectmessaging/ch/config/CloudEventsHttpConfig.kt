package com.kinectmessaging.ch.config
//
//import io.cloudevents.spring.webflux.CloudEventHttpMessageReader
//import io.cloudevents.spring.webflux.CloudEventHttpMessageWriter
//import org.springframework.boot.web.codec.CodecCustomizer
//import org.springframework.context.annotation.Configuration
//import org.springframework.http.codec.CodecConfigurer
//
//@Configuration
//class CloudEventsHttpConfig: CodecCustomizer {
//    override fun customize(configurer: CodecConfigurer) {
//        configurer.customCodecs().register(CloudEventHttpMessageReader())
//        configurer.customCodecs().register(CloudEventHttpMessageWriter())
//    }
//}