package com.devaffeine.auth.dto

import com.devaffeine.auth.domain.AuthUser

class UserRequest(val name: String, username: String, password: String) : AuthRequest(username, password) {
    fun toUser(): AuthUser {
        return AuthUser(name = name, username = username, password = password)
    }
}
