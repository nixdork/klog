package org.nixdork.klog.adapters.model

import org.nixdork.klog.common.Roles
import java.time.OffsetDateTime
import java.util.UUID

data class VerifyLoginModel(
    val id: UUID,
    val email: String,
    val hash: String?,
    val salt: String?,
    val role: Roles,
    val lastLoginAt: OffsetDateTime? = null,
)