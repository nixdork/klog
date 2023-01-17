package org.nixdork.klog.adapters.model

import java.time.OffsetDateTime
import org.nixdork.klog.common.Roles
import java.util.UUID

data class PersonModel(
    val id: UUID,
    val name: String?,
    val email: String,
    val passwordAt: OffsetDateTime? = null,
    val role: Roles,
    val uri: String? = null,
    val avatar: String? = null,
    val lastLoginAt: OffsetDateTime? = null,
    val createdAt: OffsetDateTime? = null,
    val updatedAt: OffsetDateTime? = null
)
