package com.devaffeine.auth.couchbase.model

import org.springframework.data.annotation.Id
import org.springframework.data.couchbase.core.index.QueryIndexed
import org.springframework.data.couchbase.core.mapping.Document
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy
import java.util.*

@Document
class AuthUser(
    @Id @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    var id: UUID? = null,

    val name: String,

    @QueryIndexed
    val username: String,

    val password: String,
)
