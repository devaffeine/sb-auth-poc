package com.devaffeine.auth.couchbase.dto

import com.devaffeine.auth.couchbase.Constants
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.OffsetDateTime

data class JwtToken(
    val token: String,
    val type: String = tokenType,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.jsonDateTimePattern)
    val createdAt: OffsetDateTime,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.jsonDateTimePattern)
    val expiresAt: OffsetDateTime
) {
    companion object {
        const val tokenType = "Bearer"
    }
}
