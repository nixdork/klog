package org.nixdork.klog.adapters.model

import java.time.OffsetDateTime
import java.util.UUID
import org.nixdork.klog.common.Roles

data class PersonLoginModel(
    val id: UUID,
    val email: String,
    val hash: String,
    val salt: String,
    val passwordAt: OffsetDateTime? = null,
    val role: Roles,
    val lastLoginAt: OffsetDateTime? = null,
    val updatedAt: OffsetDateTime? = null
)
