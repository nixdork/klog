package org.nixdork.klog.frameworks.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "service")
data class ServiceProperties(
    val name: String,
    val environment: String
)

@Validated
@ConfigurationProperties(prefix = "klog")
data class KlogProperties(
    val title: String,
    val subtitle: String,
    val domain: String,
    val primaryAuthor: String,
    val generator: String,
    val icon: String,
    val logo: String,
    val style: String,
    val rights: String,
    val homeEntries: Int
)
