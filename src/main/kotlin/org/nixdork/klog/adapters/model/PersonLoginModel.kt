package org.nixdork.klog.adapters.model

import org.nixdork.klog.common.Roles
import java.time.OffsetDateTime
import java.util.UUID

data class PersonLoginModel(
    val id: UUID,
    val email: String,
    val password: String,
    val passwordAt: OffsetDateTime? = null,
    val role: Roles,
    val lastLoginAt: OffsetDateTime? = null,
    val updatedAt: OffsetDateTime? = null,
)
