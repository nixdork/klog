package org.nixdork.klog.adapters.model

import java.util.UUID

data class PersonModel(
    val id: UUID,
    val name: String,
    val email: String,
    val uri: String? = null
)
