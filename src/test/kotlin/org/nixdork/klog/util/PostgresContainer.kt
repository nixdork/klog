package org.nixdork.klog.util

import io.kotest.core.extensions.install
import io.kotest.core.spec.Spec
import io.kotest.extensions.testcontainers.SharedJdbcDatabaseContainerExtension
import io.mockk.core.ValueClassSupport.boxedValue
import java.time.OffsetDateTime
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.nixdork.klog.adapters.model.EntryModel
import org.nixdork.klog.adapters.model.PersonModel
import org.nixdork.klog.adapters.model.TagModel
import org.nixdork.klog.frameworks.data.dao.Entries
import org.nixdork.klog.frameworks.data.dao.EntriesMetadata
import org.nixdork.klog.frameworks.data.dao.EntriesToTags
import org.nixdork.klog.frameworks.data.dao.People
import org.nixdork.klog.frameworks.data.dao.Tags
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

internal fun insertTag(tag: TagModel) {
    transaction {
        Tags.insert {
            it[id] = tag.id
            it[term] = tag.term
            it[permalink] = tag.permalink
        }
    }
}

internal fun insertPerson(person: PersonModel, salt: String, hash: String) {
    transaction {
        People.insert {
            it[id] = person.id
            it[email] = person.email
            it[name] = person.name
            it[role] = person.role
            it[uri] = person.uri
            it[avatar] = person.avatar
            it[pwat] = OffsetDateTime.now().toInstant()
            it[People.salt] = salt
            it[People.hash] = hash
        }
    }
}

internal fun insertEntry(entry: EntryModel) {
    transaction {
        Entries.insert {
            it[id] = entry.id
            it[slug] = entry.slug!!
            it[permalink] = entry.permalink
            it[title] = entry.title
            it[draft] = entry.draft
            it[publishedAt] = entry.publishedAt?.toInstant()
            it[primaryAuthor] = entry.primaryAuthor.id
            it[content] = entry.content
            it[summary] = entry.summary
        }
        entry.metadata?.forEach { metadata ->
            EntriesMetadata.insert {
                it[id] = metadata.id
                it[entryId] = metadata.entryId
                it[key] = metadata.key
                it[value] = metadata.value
            }
        }
        entry.tags.forEach { tag ->
            EntriesToTags.insert {
                it[entryId] = entry.id
                it[tagId] = tag.id
            }
        }
    }
}
