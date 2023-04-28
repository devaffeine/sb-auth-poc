package com.devaffeine.auth.couchbase.repository

import com.devaffeine.auth.couchbase.model.AuthUser
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.UUID

@Repository
interface UserRepository : ReactiveCrudRepository<AuthUser, UUID> {
    fun findByUsername(username: String): Mono<AuthUser>
}
