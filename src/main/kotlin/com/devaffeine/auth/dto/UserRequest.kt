package com.devaffeine.auth.dto

import com.devaffeine.auth.domain.AuthUser
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

class UserRequest(
    @NotEmpty
    @Size(max = 255)
    val name: String,

    username: String,
    password: String,
) : AuthRequest(username, password) {
    fun toUser(): AuthUser {
        return AuthUser(name = name, username = username, password = password)
    }
}
