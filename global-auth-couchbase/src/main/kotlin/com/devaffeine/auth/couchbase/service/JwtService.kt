package com.devaffeine.auth.couchbase.service

import com.devaffeine.auth.couchbase.dto.JwtToken
import com.devaffeine.auth.couchbase.model.AuthUser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.security.Key
import java.time.OffsetDateTime
import java.util.*
import kotlin.random.Random

@Service
class JwtService(
    @Value("\${jwt.secret-key:}") private val secretKey: String,
    @Value("\${jwt.issuer:devaffeine}") private val issuer: String,
) {
    private final val signInKey: Key

    init {
        var key = secretKey.trim()
        if (key.isEmpty()) {
            val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            key = (1..64)
                .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
                .joinToString("")
        }
        val keyBytes = Decoders.BASE64.decode(key)
        signInKey = Keys.hmacShaKeyFor(keyBytes)
    }

    fun createToken(authUser: AuthUser): Mono<JwtToken> {
        val now = OffsetDateTime.now()
        val expiresAt = now.plusDays(20)
        val token = Jwts.builder()
            .setIssuer(issuer)
            .setIssuedAt(Date.from(now.toInstant()))
            .setSubject(authUser.username)
            .setExpiration(Date.from(expiresAt.toInstant()))
            .signWith(signInKey, SignatureAlgorithm.HS256)
            .compact()
        return Mono.just(JwtToken(token, createdAt = now, expiresAt = expiresAt))
    }

    fun parseUsername(token: String?): Mono<String> {
        if (token != null) {
            val jwt = Jwts.parserBuilder()
                .setSigningKey(signInKey)
                .requireIssuer(issuer)
                .build()
                .parseClaimsJws(token)
            if (jwt.body.expiration.after(Date())) {
                return Mono.just(jwt.body.subject)
            }
        }
        return Mono.empty()
    }
}
