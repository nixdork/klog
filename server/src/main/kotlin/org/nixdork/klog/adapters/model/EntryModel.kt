package org.nixdork.klog.adapters.model

import org.nixdork.klog.common.slugify
import java.time.OffsetDateTime
import java.util.UUID

data class EntryModel(
    val id: UUID,
    val title: String,
    val slug: String? = title.slugify(),
    val permalink: String,
    val draft: Boolean = true,
    val createdAt: OffsetDateTime? = null,
    val updatedAt: OffsetDateTime? = null,
    val publishedAt: OffsetDateTime? = null,
    val primaryAuthor: PersonModel,
    val content: String,
    val summary: String? = null,
    val tags: Set<TagModel>,
    val metadata: Set<EntryMetadataModel>? = null,
)
