package com.devaffeine.auth.model

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("auth_user")
class AuthUser(
    @Transient
    var _id: UUID? = null,

    @NotEmpty
    @Size(max = 255)
    val name: String,

    @NotEmpty
    @Size(max = 255)
    val username: String,

    @NotEmpty
    @Size(max = 255)
    val password: String,

    @Transient
    val _isNew : Boolean
) : Persistable<UUID> {
    @Id
    override fun getId(): UUID? {
        if(_id == null) {
            _id = UUID.randomUUID();
        }
        return _id;
    }

    @Transient
    override fun isNew(): Boolean {
        return _isNew;
    }
}
