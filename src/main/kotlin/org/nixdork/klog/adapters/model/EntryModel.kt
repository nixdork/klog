package org.nixdork.klog.adapters.model

import org.nixdork.klog.common.slugify
import java.time.OffsetDateTime
import java.util.UUID

data class EntryModel(
    val id: UUID,
    val title: String,
    val slug: String = title.slugify(),
    val draft: Boolean = true,
    val updated: OffsetDateTime = OffsetDateTime.now(),
    val published: OffsetDateTime? = null,
    val primaryAuthor: PersonModel,
    val content: String,
    val summary: String? = null,
    val rights: String? = null
)
