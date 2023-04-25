package com.devaffeine.auth.repository.rw

import com.devaffeine.auth.model.AuthUser
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface UserRwRepository : ReactiveCrudRepository<AuthUser, Long> {
}
