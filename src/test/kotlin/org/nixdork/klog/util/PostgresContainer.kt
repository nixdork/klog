package org.nixdork.klog.util

import io.kotest.core.extensions.install
import io.kotest.core.spec.Spec
import io.kotest.extensions.testcontainers.SharedJdbcDatabaseContainerExtension
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer

private val tables = listOf("entry_metadata", "entry_to_tag", "entry", "tag", "person")

val postgres = PostgreSQLContainer<Nothing>(TC_PG_IMAGE).apply {
    withDatabaseName(TC_DATABASE)
    withUsername(TC_USER)
    withPassword(TC_PASSWD)
    withInitScript(TC_INITSCRIPT)
}

val extension = SharedJdbcDatabaseContainerExtension(postgres) {
    schema = TC_SCHEMA
    maximumPoolSize = TC_MAX_POOL_SIZE
}

fun Spec.installPostgres() {
    install(extension).also {
        Database.connect(it)
    }

    afterSpec {
        transaction {
            TransactionManager.current().connection
                .prepareStatement("truncate ${tables.joinToString(", ")};", false)
                .executeUpdate()
        }
    }
}
