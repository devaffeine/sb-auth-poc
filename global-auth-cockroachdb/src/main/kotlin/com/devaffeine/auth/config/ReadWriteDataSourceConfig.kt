package com.devaffeine.auth.config

import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.core.DefaultReactiveDataAccessStrategy
import org.springframework.data.r2dbc.core.R2dbcEntityOperations
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.dialect.MySqlDialect
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.core.DatabaseClient

@Configuration
@EnableR2dbcRepositories(
    entityOperationsRef = "rwEntityTemplate",
    basePackages = [
        "com.devaffeine.auth.repository.rw"
    ]
)
class ReadWriteDataSourceConfig {
    @Bean
    @Qualifier(value = "rwConnectionFactory")
    fun rwConnectionFactory(config: ReadWriteConfig): ConnectionFactory {
        val builder = ConnectionFactoryOptions.builder().from(ConnectionFactoryOptions.parse(config.url))
        if (config.username != null) {
            builder.option(ConnectionFactoryOptions.USER, config.username)
        }
        if (config.password != null) {
            builder.option(ConnectionFactoryOptions.PASSWORD, config.password)
        }
        val factory = ConnectionFactories.get(builder.build())
        val factoryBuilder = ConnectionPoolConfiguration.builder(factory)
        if (config.pool != null) {
            factoryBuilder
                .initialSize(config.pool.initialSize)
                .maxSize(config.pool.maxSize)
            if (config.pool.maxIdleTime != null) {
                factoryBuilder.maxIdleTime(config.pool.maxIdleTime)
            }
        }
        return ConnectionPool(factoryBuilder.build())
    }

    @Bean
    fun rwEntityTemplate(@Qualifier("rwConnectionFactory") factory: ConnectionFactory): R2dbcEntityOperations {
        val dialect = MySqlDialect.INSTANCE // todo: from config
        val strategy = DefaultReactiveDataAccessStrategy(dialect)
        val databaseClient = DatabaseClient.builder()
            .connectionFactory(factory)
            .bindMarkers(dialect.bindMarkersFactory)
            .build();
        return R2dbcEntityTemplate(databaseClient, strategy)
    }
}
