package com.devaffeine.auth.controller

import com.devaffeine.auth.domain.AuthUser
import com.devaffeine.auth.dto.AuthRequest
import com.devaffeine.auth.dto.JwtToken
import com.devaffeine.auth.dto.UserRequest
import com.devaffeine.auth.dto.UserResponse
import com.devaffeine.auth.service.UserService
import com.devaffeine.auth.service.JwtService
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@RestController
class AuthController(val userService: UserService, val jwtService: JwtService) {
    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    fun signUp(@RequestBody userRequest: UserRequest): Mono<JwtToken> {
        return userService.saveUser(userRequest.toUser())
            .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user")))
            .flatMap { user -> jwtService.createToken(user) }
    }

    @PostMapping("/sign-in")
    fun signIn(@RequestBody auth: AuthRequest): Mono<JwtToken> {
        return userService.findUserByUsernameAndPassword(auth.username, auth.password)
            .switchIfEmpty(Mono.error(ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")))
            .flatMap { user -> jwtService.createToken(user) }
    }

    @GetMapping("/me")
    fun me(@AuthenticationPrincipal authUser: AuthUser): Mono<UserResponse> {
        return Mono.just(UserResponse(authUser))
    }
}
