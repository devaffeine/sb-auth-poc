package com.devaffeine.auth.dto

import com.devaffeine.auth.domain.AuthUser

data class UserResponse(val name: String, val username: String) {
    constructor(authUser: AuthUser) : this(authUser.name, authUser.username)
}
