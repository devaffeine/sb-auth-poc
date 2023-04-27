package com.devaffeine.auth.repository

import com.devaffeine.auth.model.AuthUser
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono
import java.util.UUID

interface UserRepository : R2dbcRepository<AuthUser, UUID> {
    fun findByUsername(username: String): Mono<AuthUser>
}
