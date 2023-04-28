package com.devaffeine.auth.couchbase

import com.devaffeine.auth.couchbase.dto.JwtToken
import com.devaffeine.auth.couchbase.exceptions.AppExceptionHandler
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.reactive.config.EnableWebFlux

@EnableWebFlux
@SpringBootApplication
class Application {
	@Bean
	fun openAPI(
			@Value("\${info.app.version:0}") appVersion: String, @Value("\${info.app.description:}") appDescription: String
	): OpenAPI {
		val info = Info().title("Auth API").description(appDescription).version("v$appVersion")
		val jwtComponent = Components().addSecuritySchemes(
				"${JwtToken.tokenType} JWT",
				SecurityScheme().type(SecurityScheme.Type.HTTP).scheme(JwtToken.tokenType).bearerFormat("JWT")
						.`in`(SecurityScheme.In.HEADER).name(HttpHeaders.AUTHORIZATION)
		)
		return OpenAPI().info(info).components(jwtComponent)
				.addSecurityItem(SecurityRequirement().addList("${JwtToken.tokenType} JWT"))
	}

	@Bean
	fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

	@Bean
	@Primary
	fun jsonMapper(): ObjectMapper = ObjectMapper()
			.setSerializationInclusion(JsonInclude.Include.NON_NULL)
			.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)

	@Bean
	@Order(-2)
	@Primary
	fun exceptionHandler(mapper: ObjectMapper): ErrorWebExceptionHandler = AppExceptionHandler(mapper)
}

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}
