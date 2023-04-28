package com.devaffeine.auth.couchbase.exceptions

import org.springframework.http.HttpStatus

abstract class AppException(val status: HttpStatus, message: String? = null, cause: Throwable? = null) :
    Exception(message, cause)
