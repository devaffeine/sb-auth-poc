package com.devaffeine.auth.service

import com.devaffeine.auth.exceptions.AppExceptionHandler
import com.devaffeine.auth.exceptions.InvalidCredentialsException
import com.devaffeine.auth.model.AuthUser
import com.devaffeine.auth.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Duration

@Service
class UserService(val userRepository: UserRepository, val passwordEncoder: PasswordEncoder) {
    fun signIn(username: String, password: String): Mono<AuthUser> {
        return userRepository.findByUsername(username)
            //.timeout(Duration.ofSeconds(1))
            .switchIfEmpty(Mono.error(InvalidCredentialsException()))
            .doOnNext {
                val passwordMatch = passwordEncoder.matches(password, it.password)
                if (!passwordMatch) {
                    throw InvalidCredentialsException()
                }
            }
    }

    fun findUserByUsername(username: String): Mono<AuthUser> {
        return userRepository.findByUsername(username)
            //.timeout(Duration.ofSeconds(1))
    }

    fun saveUser(authUser: AuthUser): Mono<AuthUser> {
        val encodedPassword = passwordEncoder.encode(authUser.password)
        val user = AuthUser(authUser.id, authUser.name, authUser.username, encodedPassword)
        return userRepository.save(user)
            //.timeout(Duration.ofSeconds(1))
            .onErrorMap(AppExceptionHandler::mapException)
    }
}
