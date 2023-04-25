package com.devaffeine.auth.repository

import com.devaffeine.auth.model.AuthUser
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface UserRepository : R2dbcRepository<AuthUser, Long> {
    fun findByUsername(username: String): Mono<AuthUser>
}