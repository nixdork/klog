package org.nixdork.klog.adapters.model

import java.util.UUID

data class LinkModel(
    val id: UUID,
    val href: String,
    val title: String? = null,
    val rel: String? = null,
    val type: String? = null,
    val hreflang: String? = null,
    val length: Long?
)
