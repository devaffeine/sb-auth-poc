package com.devaffeine.auth.couchbase.service

import com.devaffeine.auth.couchbase.exceptions.AppExceptionHandler
import com.devaffeine.auth.couchbase.exceptions.InvalidCredentialsException
import com.devaffeine.auth.couchbase.model.AuthUser
import com.devaffeine.auth.couchbase.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserService(
    val userRepo: UserRepository,
    val passwordEncoder: PasswordEncoder
) {
    fun signIn(username: String, password: String): Mono<AuthUser> {
        return userRepo.findByUsername(username)
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
        return userRepo.findByUsername(username)
        //.timeout(Duration.ofSeconds(1))
    }

    fun saveUser(authUser: AuthUser): Mono<AuthUser> {
        val encodedPassword = passwordEncoder.encode(authUser.password)
        val user = AuthUser(authUser.id, authUser.name, authUser.username, encodedPassword)
        return userRepo.save(user)
            //.timeout(Duration.ofSeconds(1))
            .onErrorMap(AppExceptionHandler::mapException)
    }
}
