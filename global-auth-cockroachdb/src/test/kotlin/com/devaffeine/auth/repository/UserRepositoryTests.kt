package com.devaffeine.auth.repository

import io.r2dbc.spi.ConnectionFactory
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.r2dbc.core.DatabaseClient
import reactor.test.StepVerifier
import java.time.Duration
import java.util.UUID

@DataR2dbcTest
class UserRepositoryTests {
    @Autowired
    lateinit var factory: ConnectionFactory

    private lateinit var client: DatabaseClient

    @Autowired
    lateinit var userRepository: UserRepository

    @BeforeEach
    fun setup() {
        client = DatabaseClient.create(factory);
    }

    @Test
    fun contextLoads() {
        assertNotNull(client)
        assertNotNull(userRepository)
    }

    @Test
    fun testQueryNotExistent() {
        val username = "user-${System.currentTimeMillis()}"
        userRepository
            .findByUsername(username)
            .take(Duration.ofSeconds(1))
            .`as`(StepVerifier::create)
            .expectNextCount(0)
            .verifyComplete()
    }

    @Test
    fun testInsertAndQuery() {
        val username = "user-${System.currentTimeMillis()}"
        val password = "password-${System.currentTimeMillis()}"
        client
            .sql("INSERT INTO auth_user (id, name, username, password) VALUES (:id, :name, :username, :password)")
            .bind("id", UUID.randomUUID())
            .bind("name", username)
            .bind("username", username)
            .bind("password", password)
            .fetch()
            .rowsUpdated()
            .`as`(StepVerifier::create)
            .expectNextCount(1)
            .verifyComplete()
        userRepository
            .findByUsername(username)
            .take(Duration.ofSeconds(1))
            .`as`(StepVerifier::create)
            .expectNextCount(1)
            .verifyComplete()
    }
}
