package org.nixdork.klog.adapters.model

import jakarta.annotation.security.PermitAll
import java.util.UUID

data class TagModel(
    val id: UUID,
    val term: String,
    val permalink: String,
    val label: String? = null,
    val scheme: String? = null
)
