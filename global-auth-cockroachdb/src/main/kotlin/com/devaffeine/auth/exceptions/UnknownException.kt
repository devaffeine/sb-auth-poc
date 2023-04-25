package com.devaffeine.auth.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
internal class UnknownException : AppException {
    companion object {
        const val defaultMessage = "Unknown error."
    }

    constructor() : super(HttpStatus.INTERNAL_SERVER_ERROR, defaultMessage)
    constructor(message: String = defaultMessage) : super(HttpStatus.INTERNAL_SERVER_ERROR, message)
    constructor(message: String = defaultMessage, cause: Throwable?) : super(HttpStatus.INTERNAL_SERVER_ERROR, message, cause)

    constructor(cause: Throwable?) : super(HttpStatus.INTERNAL_SERVER_ERROR, defaultMessage, cause)
}
