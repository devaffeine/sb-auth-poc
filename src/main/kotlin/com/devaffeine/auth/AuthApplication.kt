package com.devaffeine.auth

import com.devaffeine.auth.dto.JwtToken
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.config.EnableWebFlux

@EnableR2dbcRepositories
@EnableWebFlux
@SpringBootApplication
class AuthApplication {
    @Bean
    fun openAPI(@Value("\${info.app.version:0}") appVersion: String): OpenAPI {
        val info = Info()
            .title("Auth API")
            .description("Spring Boot Auth PoC project")
            .version("v$appVersion")
        val jwtComponent = Components().addSecuritySchemes(
            "${JwtToken.tokenType} JWT",
            SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme(JwtToken.tokenType).bearerFormat("JWT")
                .`in`(SecurityScheme.In.HEADER)
                .name(HttpHeaders.AUTHORIZATION)
        )
        return OpenAPI()
            .info(info)
            .components(jwtComponent)
            .addSecurityItem(SecurityRequirement().addList("${JwtToken.tokenType} JWT"))
    }
}

fun main(args: Array<String>) {
    runApplication<AuthApplication>(*args)
}
