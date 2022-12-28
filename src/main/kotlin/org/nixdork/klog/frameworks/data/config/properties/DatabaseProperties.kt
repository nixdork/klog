package org.nixdork.klog.frameworks.data.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import javax.validation.constraints.Pattern

@ConfigurationProperties(prefix = "database")
data class DatabaseProperties(
    val name: String,
    val username: String,
    val password: String,
    val host: String,
    val port: String,
    val schema: String,
    @Pattern(regexp = "\\d{1,2}")
    val connectionPoolSize: String,
)
