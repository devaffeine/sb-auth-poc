package com.devaffeine.auth.controller

import com.devaffeine.auth.dto.AuthRequest
import com.devaffeine.auth.dto.UserRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@SpringBootTest
class AuthControllerTests {
    @Autowired
    lateinit var controller: AuthController

    @Autowired
    lateinit var mapper: ObjectMapper

    lateinit var client: WebTestClient

    @BeforeEach
    fun setup() {
        client = WebTestClient.bindToController(controller).build()
    }

    @Test
    fun contextLoads() {
        Assertions.assertNotNull(controller)
        Assertions.assertNotNull(mapper)
    }

    @Test
    fun whenSignUp_thenShouldGetCreatedStatus() {
        val userRequest = randomUser()
        val request = mapper.writeValueAsString(userRequest)
        client.post()
            .uri("/sign-up")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .exchange()
            .expectStatus()
            .isCreated
    }

    @Test
    fun whenSignUp_thenShouldNotGetCreatedStatus() {
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
    fun whenSign_withInvalidCredentials_thenShouldThrow401() {
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
    fun whenSign_withValidCredentials_thenShouldThrow200() {
        val userRequest = randomUser()
        val request = mapper.writeValueAsString(userRequest)
        client.post()
                .uri("/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus()
                .isCreated

        val exch = client.post()
                .uri("/sign-in")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()

        exch.expectStatus().isOk
        exch.expectBody().jsonPath("\$.token").isNotEmpty
                         .jsonPath("\$.expiresAt").isNotEmpty
    }

    companion object {
        fun randomUser() = UserRequest(
                name = System.currentTimeMillis().toString(),
                username = System.currentTimeMillis().toString(),
                password = System.currentTimeMillis().toString()
        )
    }
}
