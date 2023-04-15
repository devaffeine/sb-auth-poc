package com.devaffeine.auth.config

import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean


internal class ReadOnlyDataSourceConfig {
    @Bean
    @ConfigurationProperties("spring.datasource.read-only")
    @Qualifier(value = "roR2dbcProperties")
    fun roR2dbcProperties(): R2dbcProperties? {
        return R2dbcProperties()
    }

    @Bean
    @Qualifier(value = "roConnectionFactory")
    fun roConnectionFactory(@Qualifier("roR2dbcProperties") props : R2dbcProperties): ConnectionFactory? {
        return null;
    }
}
