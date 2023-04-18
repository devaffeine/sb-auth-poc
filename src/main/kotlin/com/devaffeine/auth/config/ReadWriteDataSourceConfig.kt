package com.devaffeine.auth.config

import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions.*
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
        val connectionFactory = ConnectionFactories.get(
            builder()
                .from(parse(config.url))
                .option(USER, config.username)
                .option(PASSWORD, config.password)
                .build()
        )
        val configuration = ConnectionPoolConfiguration.builder(connectionFactory)
            .maxIdleTime(config.pool.maxIdleTime)
            .initialSize(config.pool.initialSize)
            .maxSize(config.pool.maxSize)
            .build()
        return ConnectionPool(configuration)
    }

    @Bean
    fun rwEntityTemplate(@Qualifier("rwConnectionFactory") factory: ConnectionFactory): R2dbcEntityOperations {
        val strategy = DefaultReactiveDataAccessStrategy(MySqlDialect.INSTANCE);
        val databaseClient = DatabaseClient.builder()
            .connectionFactory(factory)
            .bindMarkers(MySqlDialect.INSTANCE.bindMarkersFactory)
            .build();
        return R2dbcEntityTemplate(databaseClient, strategy);
    }
}
