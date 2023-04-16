package com.devaffeine.auth.config

import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import io.r2dbc.spi.ConnectionFactoryOptions.*
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
@EnableR2dbcRepositories(entityOperationsRef = "rwEntityTemplate")
class ReadWriteDataSourceConfig {
    @Bean
    @ConfigurationProperties("spring.datasource.read-write")
    @Qualifier(value = "rwR2dbcProperties")
    fun rwR2dbcProperties(): R2dbcProperties? {
        return R2dbcProperties()
    }

    @Bean
    @Qualifier(value = "rwConnectionFactory")
    fun rwConnectionFactory(@Qualifier("rwR2dbcProperties") props : R2dbcProperties): ConnectionFactory? {
        val connectionFactory = ConnectionFactories.get(builder()
                .from(parse(props.url))
                .option(USER, props.username)
                .option(PASSWORD, props.password)
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
    fun rwEntityTemplate(@Qualifier("rwConnectionFactory") connectionFactory : ConnectionFactory) : R2dbcEntityOperations {
        val strategy = DefaultReactiveDataAccessStrategy(MySqlDialect.INSTANCE);
        val databaseClient = DatabaseClient.builder()
                .connectionFactory(connectionFactory)
                .bindMarkers(MySqlDialect.INSTANCE.bindMarkersFactory)
                .build();

        return R2dbcEntityTemplate(databaseClient, strategy);
    }
}