package com.devaffeine.auth.couchbase.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration
import org.springframework.data.couchbase.repository.config.EnableReactiveCouchbaseRepositories

@Configuration
@EnableReactiveCouchbaseRepositories
class CouchbaseConfig(
        @param:Value("\${spring.couchbase.connection-string}") private val connectionString: String,
        @param:Value("\${spring.couchbase.username}") private val username: String,
        @param:Value("\${spring.couchbase.password}") private val password: String,
        @param:Value("\${spring.couchbase.bucket-name}") private val bucketName: String) : AbstractCouchbaseConfiguration() {
    override fun getConnectionString(): String {
        return connectionString
    }

    override fun getUserName(): String {
        return username
    }

    override fun getPassword(): String {
        return password
    }

    override fun getBucketName(): String {
        return bucketName
    }
}
