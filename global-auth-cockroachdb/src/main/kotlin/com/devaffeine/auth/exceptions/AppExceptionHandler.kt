package com.devaffeine.auth.exceptions

import com.devaffeine.auth.controller.AuthController
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.MediaType
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class AppExceptionHandler(private val objectMapper: ObjectMapper) : ErrorWebExceptionHandler {
    companion object {
        private val logger = LoggerFactory.getLogger(AppExceptionHandler::class.java)
        fun mapException(e: Throwable): AppException {
            return when (e) {
                is DuplicateKeyException -> UsernameAlreadyExistsException(e)
                else -> {
                    logger.error(e.message, e);
                    UnknownException(e)
                }
            }
        }
    }

    override fun handle(serverWebExchange: ServerWebExchange, e: Throwable): Mono<Void> {
        val bufferFactory = serverWebExchange.response.bufferFactory()
        val throwable = mapException(e)
        serverWebExchange.response.setStatusCode(throwable.status)
        val dataBuffer = try {
            bufferFactory.wrap(objectMapper.writeValueAsBytes(HttpError(throwable.message ?: "-")))
        } catch (e: JsonProcessingException) {
            bufferFactory.wrap("-".toByteArray())
        }
        serverWebExchange.response.headers.contentType = MediaType.APPLICATION_JSON
        return serverWebExchange.response.writeWith(Mono.just(dataBuffer))
    }

    inner class HttpError internal constructor(val message: String)
}
