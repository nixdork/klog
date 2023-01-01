package org.nixdork.klog.adapters.model

import java.time.OffsetDateTime
import java.util.UUID

data class EntryMetadataModel(
    val id: UUID,
    val key: String,
    val value: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime? = null
)
