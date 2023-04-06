package com.devaffeine.auth.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.CONFLICT)
internal class UsernameAlreadyExistsException : AppException {
    companion object {
        const val defaultMessage = "Username already exists."
    }

    constructor() : super(HttpStatus.CONFLICT, defaultMessage)
    constructor(message: String = defaultMessage) : super(HttpStatus.CONFLICT, message)
    constructor(message: String = defaultMessage, cause: Throwable?) : super(HttpStatus.CONFLICT, message, cause)
    constructor(cause: Throwable?) : super(HttpStatus.CONFLICT, defaultMessage, cause)
}
