package org.nixdork.klog.common

import java.text.Normalizer
import java.util.UUID
import org.nixdork.klog.frameworks.data.config.properties.DatabaseProperties

fun String.toUUID(): UUID = UUID.fromString(this)

fun String.slugify(): String = Normalizer.normalize(this, Normalizer.Form.NFD)
    .replace("[^\\w\\s-]".toRegex(), "")
    .replace('-', ' ')
    .trim()
    .replace("\\s+".toRegex(), "-")
    .lowercase()

// Klog Extensions
fun DatabaseProperties.toJdbcUrl(): String {
    val root = "jdbc:postgres://$host:$port/$name"
    val paramsString = mapOf("prepareThreshold" to 0).map { (key, value) -> "$key=$value" }.joinToString("&")
    return "$root?$paramsString"
}
