package com.devaffeine.auth.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("auth_user")
class AuthUser(@Id var id: Long? = null, val name: String, val username: String, val password: String)
