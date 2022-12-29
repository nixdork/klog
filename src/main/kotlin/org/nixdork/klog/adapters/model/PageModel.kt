package org.nixdork.klog.adapters.model

import java.time.OffsetDateTime
import java.util.UUID

data class PageModel(
    val id: UUID,
    val title: String,
    val path: String,
    val linkId: LinkModel,
    val updated: OffsetDateTime = OffsetDateTime.now(),
    val published: OffsetDateTime?,
    val primaryAuthor: PersonModel,
    val content: String,
    val rights: String? = null
)
