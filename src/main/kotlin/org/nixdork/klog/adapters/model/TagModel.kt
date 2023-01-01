package org.nixdork.klog.adapters.model

import java.time.OffsetDateTime
import java.util.UUID

data class TagModel(
    val id: UUID,
    val term: String,
    val permalink: String,
    val entries: List<EntryModel>,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime? = null
)
