package org.nixdork.klog.frameworks.data.dao

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import org.nixdork.klog.adapters.model.EntryModel
import org.nixdork.klog.common.toOffsetDateTime
import java.time.OffsetDateTime
import java.util.UUID

object Entries : UUIDTable(name = "entry") {
    val slug = text("slug")
    val permalink = text("permalink")
    val title = text("title")
    val draft = bool("draft")
    val createdAt = timestamp("created_at").default(OffsetDateTime.now().toInstant())
    val updatedAt = timestamp("updated_at").nullable()
    val publishedAt = timestamp("published_at").nullable()
    val primaryAuthor = reference("author_id", People)
    val content = text("content")
    val summary = text("summary").nullable()
}

class Entry(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<Entry>(Entries)

    var slug by Entries.slug
    var title by Entries.title
    var permalink by Entries.permalink
    var draft by Entries.draft
    var createdAt by Entries.createdAt
    var updatedAt by Entries.updatedAt
    var publishedAt by Entries.publishedAt
    var primaryAuthor by Person referencedOn Entries.primaryAuthor
    var content by Entries.content
    var summary by Entries.summary

    var tags by Tag via EntriesToTags
    val metadata by EntryMetadata referrersOn EntriesMetadata.entry

    fun toModel(): EntryModel =
        EntryModel(
            id = this.id.value,
            title = this.title,
            slug = this.slug,
            permalink = this.permalink,
            draft = this.draft,
            createdAt = this.createdAt.toOffsetDateTime(),
            updatedAt = this.updatedAt?.toOffsetDateTime(),
            publishedAt = this.publishedAt?.toOffsetDateTime(),
            primaryAuthor = this.primaryAuthor.toModel(),
            content = this.content,
            summary = this.summary,
            tags = this.tags.map { it.toModel() },
            metadata = this.metadata.map { it.toModel() }
        )
}
