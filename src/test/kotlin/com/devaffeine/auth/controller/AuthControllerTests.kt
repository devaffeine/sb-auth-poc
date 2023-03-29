package com.devaffeine.auth.controller

import com.devaffeine.auth.dto.AuthRequest
import com.devaffeine.auth.dto.UserRequest
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
class AuthControllerTests {
    @Autowired
    lateinit var controller: AuthController

    lateinit var mapper: ObjectMapper

    lateinit var client: WebTestClient

    companion object {
        fun randomUser(name: String? = null) = UserRequest(
            name = name ?: System.currentTimeMillis().toString(),
            username = System.currentTimeMillis().toString(),
            password = System.currentTimeMillis().toString()
        )
    }

    @BeforeEach
    fun setup() {
        mapper = ObjectMapper()
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
        client = WebTestClient.bindToController(controller).build()
    }

    @Test
    fun contextLoads() {
        Assertions.assertNotNull(controller)
        Assertions.assertNotNull(mapper)
    }

    @Test
    fun whenSignUp_thenShouldGetCreatedStatusAndToken() {
        val userRequest = randomUser()
        val request = mapper.writeValueAsString(userRequest)
        val exchange = client.post()
            .uri("/sign-up")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
        exchange.expectStatus().isCreated
        exchange.expectBody()
            .jsonPath("\$.token").isNotEmpty
            .jsonPath("\$.expiresAt").isNotEmpty
    }

    @Test
    fun whenSignUp_withTakenUsername_thenShouldThrow4xx() {
        val userRequest = randomUser()
        val request = mapper.writeValueAsString(userRequest)
        client.post()
            .uri("/sign-up")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isCreated

        client.post()
            .uri("/sign-up")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .is4xxClientError
    }

    @Test
    fun whenSignUp_withoutName_thenShouldThrow4xx() {
        val userRequest = randomUser(name = "")
        val request = mapper.writeValueAsString(userRequest)
        client.post()
            .uri("/sign-up")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .is4xxClientError
    }

    @Test
    fun whenSignIn_withInvalidCredentials_thenShouldThrow401() {
        val username = System.currentTimeMillis().toString()
        val authRequest = AuthRequest(username, username)
        val request = mapper.writeValueAsString(authRequest)
        client.post()
            .uri("/sign-in")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isUnauthorized
    }

    @Test
    fun whenSignIn_thenShouldGetOkStatusAndToken() {
        val userRequest = randomUser()
        val request = mapper.writeValueAsString(userRequest)
        client.post()
            .uri("/sign-up")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isCreated

        val exchange = client.post()
            .uri("/sign-in")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
        exchange.expectStatus().isOk
        exchange.expectBody()
            .jsonPath("\$.token").isNotEmpty
            .jsonPath("\$.expiresAt").isNotEmpty
    }
}
