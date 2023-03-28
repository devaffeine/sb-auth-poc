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
        val username = System.currentTimeMillis().toString()
        val userRequest = UserRequest(username, username, username)
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
}
