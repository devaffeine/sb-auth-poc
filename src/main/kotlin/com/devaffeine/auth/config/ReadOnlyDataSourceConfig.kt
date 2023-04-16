package com.devaffeine.auth.config

import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.core.DefaultReactiveDataAccessStrategy
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.dialect.MySqlDialect
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.core.DatabaseClient
import java.time.Duration

@Configuration
@EnableR2dbcRepositories(entityOperationsRef = "roEntityTemplate")
class ReadOnlyDataSourceConfig {
    @Bean
    @ConfigurationProperties("spring.datasource.read-only")
    @Qualifier(value = "roR2dbcProperties")
    fun roR2dbcProperties(): R2dbcProperties? {
        return R2dbcProperties()
    }

    @Bean
    @Qualifier(value = "roConnectionFactory")
    fun roConnectionFactory(@Qualifier("roR2dbcProperties") props : R2dbcProperties): ConnectionFactory? {
        val connectionFactory = ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .from(ConnectionFactoryOptions.parse(props.url))
                .option(ConnectionFactoryOptions.USER, props.username)
                .option(ConnectionFactoryOptions.PASSWORD, props.password)
                .build())
        val configuration = ConnectionPoolConfiguration.builder(connectionFactory)
                .maxIdleTime(props.pool.maxIdleTime)
                .initialSize(props.pool.initialSize)
                .maxSize(props.pool.maxSize)
                .maxCreateConnectionTime(Duration.ofSeconds(1))
                .build()
        return ConnectionPool(configuration)
    }

    @Bean
    fun roEntityTemplate(@Qualifier("roConnectionFactory") connectionFactory : ConnectionFactory) : R2dbcEntityOperations {
        val strategy = DefaultReactiveDataAccessStrategy(MySqlDialect.INSTANCE)
        val databaseClient = DatabaseClient.builder()
                .connectionFactory(connectionFactory)
                .bindMarkers(MySqlDialect.INSTANCE.bindMarkersFactory)
                .build()

        return R2dbcEntityTemplate(databaseClient, strategy);
    }
}
