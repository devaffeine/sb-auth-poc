package com.devaffeine.auth.dto

import com.devaffeine.auth.model.AuthUser

data class UserResponse(val name: String, val username: String) {
    constructor(authUser: AuthUser) : this(authUser.name, authUser.username)
}
