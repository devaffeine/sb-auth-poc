package com.devaffeine.auth.couchbase.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED)
internal class InvalidCredentialsException : AppException {
    companion object {
        const val defaultMessage = "Invalid credentials."
    }

    constructor() : super(HttpStatus.UNAUTHORIZED, defaultMessage)
    constructor(message: String = defaultMessage) : super(HttpStatus.UNAUTHORIZED, message)
    constructor(message: String = defaultMessage, cause: Throwable?) : super(HttpStatus.UNAUTHORIZED, message, cause)

    constructor(cause: Throwable?) : super(HttpStatus.UNAUTHORIZED, defaultMessage, cause)
}
