package org.nixdork.klog.frameworks.config

import java.util.UUID

data class KlogConfiguration(
    val title: String,
    val subtitle: String,
    val domain: String,
    val primaryAuthor: UUID,
    val generator: String,
    val icon: String,
    val logo: String,
    val style: String,
    val rights: String,
    val homeEntries: Int,
    val passwordAge: Int
)
