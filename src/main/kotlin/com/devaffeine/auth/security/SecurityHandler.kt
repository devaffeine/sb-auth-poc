package com.devaffeine.auth.security

import com.devaffeine.auth.dto.JwtToken
import com.devaffeine.auth.model.JwtAuth
import com.devaffeine.auth.service.UserService
import com.devaffeine.auth.service.JwtService
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class SecurityHandler(val jwtService: JwtService, val userService: UserService) : ReactiveAuthenticationManager,
    ServerSecurityContextRepository {
    override fun authenticate(authentication: Authentication?): Mono<Authentication> {
        if (authentication is JwtAuth) {
            return jwtService.parseUsername(authentication.token)
                .switchIfEmpty(Mono.empty())
                .flatMap { userService.findUserByUsername(it) }
                .switchIfEmpty(Mono.empty())
                .map { UsernamePasswordAuthenticationToken(it, null, emptyList()) }
        }
        return Mono.justOrEmpty(authentication)
    }

    override fun save(exchange: ServerWebExchange, context: SecurityContext): Mono<Void> {
        throw UnsupportedOperationException("Not supported yet.")
    }

    override fun load(exchange: ServerWebExchange): Mono<SecurityContext> {
        val tokenType = "${JwtToken.tokenType} "
        return Mono.justOrEmpty(exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION))
            .filter { authHeader -> authHeader.startsWith(tokenType) }
            .flatMap {
                val auth = JwtAuth(it.substring(tokenType.length))
                authenticate(auth).map { authentication ->
                    val securityContext = SecurityContextImpl()
                    securityContext.authentication = authentication
                    securityContext
                }
            }
    }
}
