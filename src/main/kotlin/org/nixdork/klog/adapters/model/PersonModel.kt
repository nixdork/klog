package org.nixdork.klog.adapters.model

import java.time.OffsetDateTime
import java.util.UUID
import org.nixdork.klog.common.Roles

data class PersonModel(
    val id: UUID,
    val name: String,
    val email: String,
    val passwordAt: OffsetDateTime? = null,
    val role: Roles,
    val uri: String? = null,
    val avatar: String? = null,
    val lastLoginAt: OffsetDateTime? = null,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime? = null
)
