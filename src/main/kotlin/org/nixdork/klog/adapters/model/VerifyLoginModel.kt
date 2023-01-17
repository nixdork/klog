package org.nixdork.klog.adapters.model

import java.time.OffsetDateTime
import java.util.UUID
import org.nixdork.klog.common.Roles

data class VerifyLoginModel(
    val id: UUID,
    val email: String,
    val hash: String?,
    val salt: String?,
    val role: Roles,
    val lastLoginAt: OffsetDateTime? = null,
)