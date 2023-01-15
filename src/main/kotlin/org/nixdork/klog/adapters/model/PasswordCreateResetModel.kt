package org.nixdork.klog.adapters.model

import java.util.UUID

data class PasswordCreateResetModel(
    val id: UUID,
    val email: String,
    val password: String,
    val newPassword: String? = null,
)
