package com.devaffeine.auth.config

import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("spring.r2dbc.read-only")
class ReadOnlyConfig : R2dbcProperties()
