package com.devaffeine.auth.service

import com.devaffeine.auth.domain.AuthUser
import com.devaffeine.auth.repository.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserService(val userRepository: UserRepository) {
    fun findUserByUsernameAndPassword(username: String, password: String): Mono<AuthUser> {
        return userRepository.findByUsernameAndPassword(username, password)
    }

    fun findUserByUsername(username: String): Mono<AuthUser> {
        return userRepository.findByUsername(username)
    }

    fun saveUser(authUser: AuthUser): Mono<AuthUser> {
        return userRepository.save(authUser)
    }
}
