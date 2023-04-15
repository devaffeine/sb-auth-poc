package com.devaffeine.auth.config

import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean

class ReadWriteDataSourceConfig {
    @Bean
    @ConfigurationProperties("spring.datasource.read-write")
    fun rwR2dbcProperties(): R2dbcProperties? {
        return R2dbcProperties()
    }
}