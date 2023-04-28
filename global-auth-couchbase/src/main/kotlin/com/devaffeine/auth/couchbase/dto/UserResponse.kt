package com.devaffeine.auth.couchbase.dto

import com.devaffeine.auth.couchbase.model.AuthUser

data class UserResponse(val name: String, val username: String) {
    constructor(authUser: AuthUser) : this(authUser.name, authUser.username)
}
