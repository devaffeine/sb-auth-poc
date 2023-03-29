package com.devaffeine.auth.repository

import com.devaffeine.auth.model.AuthUser
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Mono

interface UserRepository : R2dbcRepository<AuthUser, Long> {
    fun findByUsernameAndPassword(username: String, password: String): Mono<AuthUser>

    fun findByUsername(username: String): Mono<AuthUser>
}
