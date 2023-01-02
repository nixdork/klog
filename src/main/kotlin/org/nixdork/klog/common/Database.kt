package org.nixdork.klog.common

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.nixdork.klog.frameworks.data.config.properties.DatabaseProperties
import org.postgresql.util.PGobject

fun DatabaseProperties.toJdbcUrl(): String {
    val root = "jdbc:postgres://$host:$port/$name"
    val paramsString = mapOf("prepareThreshold" to 0).map { (key, value) -> "$key=$value" }.joinToString("&")
    return "$root?$paramsString"
}

fun <T : Table> T.upsert(body: T.(UpdateBuilder<*>) -> Unit) = this.replace(body)

class PgEnum<T : Enum<T>>(enumType: String, enumValue: T?) : PGobject() {
    init {
        value = enumValue?.name
        type = enumType
    }
}
