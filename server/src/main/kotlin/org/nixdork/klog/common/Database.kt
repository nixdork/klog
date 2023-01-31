package org.nixdork.klog.common

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Function
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.nixdork.klog.frameworks.data.config.properties.DatabaseProperties
import org.postgresql.util.PGobject
import java.time.OffsetDateTime

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

fun java.sql.Date.toOffsetDateTime(): OffsetDateTime = toInstant().toOffsetDateTime().normalize()

fun java.sql.Timestamp.toOffsetDateTime(): OffsetDateTime = toInstant().toOffsetDateTime().normalize()

fun <T : Table> T.offsetDateTime(name: String): Column<OffsetDateTime> =
    registerColumn(name, OffsetDateTimeColumnType())

class OffsetDateTimeColumnType : ColumnType() {
    override fun sqlType(): String = "timestamptz(3)"

    override fun valueFromDB(value: Any): Any =
        when (value) {
            is java.sql.Date -> value.toOffsetDateTime()
            is java.sql.Timestamp -> value.toOffsetDateTime()
            else -> value
        }
}

class CurrentOffsetDateTime : Function<OffsetDateTime>(OffsetDateTimeColumnType()) {
    override fun toQueryBuilder(queryBuilder: QueryBuilder) =
        queryBuilder {
            append("current_timestamp")
        }
}
