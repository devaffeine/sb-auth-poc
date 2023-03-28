package com.devaffeine.auth.controller

import com.devaffeine.auth.domain.AuthUser
import com.devaffeine.auth.dto.AuthRequest
import com.devaffeine.auth.dto.JwtToken
import com.devaffeine.auth.dto.UserRequest
import com.devaffeine.auth.dto.UserResponse
import com.devaffeine.auth.exceptions.InvalidCredentialsException
import com.devaffeine.auth.service.JwtService
import com.devaffeine.auth.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
@Tag(name = "auth", description = "Auth API")
class AuthController(val userService: UserService, val jwtService: JwtService) {
    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "User sign up", responses = [
            ApiResponse(responseCode = "201", description = "User sign up and get access token"),
            ApiResponse(responseCode = "409", description = "If requested username is already registered"),
        ]
    )
    fun signUp(@RequestBody userRequest: UserRequest): Mono<JwtToken> {
        return userService.saveUser(userRequest.toUser())
            .flatMap { user -> jwtService.createToken(user) }
    }

    @PostMapping("/sign-in")
    @Operation(
        summary = "User sign in", responses = [
            ApiResponse(responseCode = "200", description = "User sign in and get access token"),
            ApiResponse(responseCode = "401", description = "If requested credentials are not valid"),
        ]
    )
    fun signIn(@RequestBody auth: AuthRequest): Mono<JwtToken> {
        return userService.findUserByUsernameAndPassword(auth.username, auth.password)
            .switchIfEmpty(Mono.error(InvalidCredentialsException("Invalid credentials")))
            .flatMap { user -> jwtService.createToken(user) }
    }

    @GetMapping("/me")
    @Operation(
        summary = "User profile", responses = [
            ApiResponse(responseCode = "200", description = "Authenticated user get profile data"),
            ApiResponse(responseCode = "401", description = "If there is no authentication in context"),
        ]
    )
    fun me(@AuthenticationPrincipal authUser: AuthUser): Mono<UserResponse> {
        return Mono.just(UserResponse(authUser))
    }
}
