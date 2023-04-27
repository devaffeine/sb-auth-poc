package com.devaffeine.auth.model

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("auth_user")
class AuthUser(
    @Id
    @Column("id")
    var id: UUID? = null,

    @NotEmpty
    @Size(max = 255)
    val name: String,

    @NotEmpty
    @Size(max = 255)
    val username: String,

    @NotEmpty
    @Size(max = 255)
    val password: String,
)
