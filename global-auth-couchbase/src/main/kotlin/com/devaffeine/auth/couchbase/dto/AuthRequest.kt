package com.devaffeine.auth.couchbase.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

open class AuthRequest(
    @NotEmpty
    @Size(max = 255)
    val username: String,

    @NotEmpty
    @Size(max = 255)
    val password: String,
)
