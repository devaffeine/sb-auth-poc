package com.devaffeine.auth.repository.ro

import com.devaffeine.auth.model.AuthUser
import org.springframework.data.repository.reactive.ReactiveSortingRepository
import reactor.core.publisher.Mono

interface UserRoRepository : ReactiveSortingRepository<AuthUser, Long> {
    fun findByUsername(username: String): Mono<AuthUser>
}
