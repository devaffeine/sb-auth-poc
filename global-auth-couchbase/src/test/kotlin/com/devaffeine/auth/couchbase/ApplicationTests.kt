package com.devaffeine.auth.couchbase

import com.devaffeine.auth.couchbase.controller.AuthController
import com.devaffeine.auth.couchbase.dto.AuthRequest
import com.devaffeine.auth.couchbase.dto.UserRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.couchbase.BucketDefinition
import org.testcontainers.couchbase.CouchbaseContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.shaded.org.awaitility.Awaitility.await
import org.testcontainers.utility.DockerImageName
import java.time.Duration


@Testcontainers
@TestConfiguration
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = [Application::class])
class ApplicationTests {

	companion object {
		private val image = DockerImageName.parse(
				"couchbase/server:enterprise-7.1.4"
		)

		private val bucketDefinition = BucketDefinition("authbucket")

		@JvmStatic
		@Container
		private var container: CouchbaseContainer = CouchbaseContainer(image)
				.withCredentials("admin", "password")
				.withBucket(bucketDefinition)
				.withStartupTimeout(Duration.ofSeconds(90))
				.waitingFor(Wait.forHealthcheck());

		@JvmStatic
		@DynamicPropertySource
		fun bindCouchbaseProperties(registry: DynamicPropertyRegistry) {
			registry.add("spring.couchbase.connection-string", container::getConnectionString)
		}

		const val signUpUri = "/sign-up"
		const val signInUri = "/sign-in"

		fun randomUser(name: String? = null) = UserRequest(
				name = name ?: System.currentTimeMillis().toString(),
				username = System.currentTimeMillis().toString(),
				password = System.currentTimeMillis().toString()
		)
	}

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
	fun whenSignUp_thenShouldGetCreatedStatusAndToken() {
		val userRequest = randomUser()
		val request = mapper.writeValueAsString(userRequest)
		val exchange = client.post()
				.uri(signUpUri)
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
				.uri(signUpUri)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.exchange()
				.expectStatus()
				.isCreated

		client.post()
				.uri(signUpUri)
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
				.uri(signUpUri)
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
				.uri(signInUri)
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
				.uri(signUpUri)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.exchange()
				.expectStatus()
				.isCreated

		val exchange = client.post()
				.uri(signInUri)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.exchange()
		exchange.expectStatus().isOk
		exchange.expectBody()
				.jsonPath("\$.token").isNotEmpty
				.jsonPath("\$.expiresAt").isNotEmpty
	}

	@Test
	fun whenSignIn_withInvalidPassword_thenShouldThrow401() {
		val userRequest = randomUser()
		val request = mapper.writeValueAsString(userRequest)
		client.post()
				.uri(signUpUri)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(request)
				.exchange()
				.expectStatus()
				.isCreated

		val authRequest = AuthRequest(userRequest.username, userRequest.password + "Invalid")
		val signInRequest = mapper.writeValueAsString(authRequest)
		client.post()
				.uri(signInUri)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(signInRequest)
				.exchange()
				.expectStatus()
				.isUnauthorized
	}
}
