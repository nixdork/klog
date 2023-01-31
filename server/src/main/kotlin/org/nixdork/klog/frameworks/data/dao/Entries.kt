package org.nixdork.klog.frameworks.data.dao

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.nixdork.klog.adapters.model.EntryModel
import org.nixdork.klog.adapters.model.TagModel
import org.nixdork.klog.common.CurrentOffsetDateTime
import org.nixdork.klog.common.offsetDateTime
import java.util.UUID

object Entries : UUIDTable(name = "entry") {
    val slug = text("slug")
    val permalink = text("permalink")
    val title = text("title")
    val draft = bool("draft")
    val createdAt = offsetDateTime("created_at").defaultExpression(CurrentOffsetDateTime())
    val updatedAt = offsetDateTime("updated_at").nullable()
    val publishedAt = offsetDateTime("published_at").nullable()
    val primaryAuthor = reference("author_id", People)
    val content = text("content")
    val summary = text("summary").nullable()
}

class Entry(id: EntityID<UUID>) : UUIDEntity(id) {
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

    val metadata by EntryMetadata referrersOn EntriesMetadata.entryId

    fun toModel(tags: Set<TagModel>): EntryModel =
        EntryModel(
            id = this.id.value,
            title = this.title,
            slug = this.slug,
            permalink = this.permalink,
            draft = this.draft,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            publishedAt = this.publishedAt,
            primaryAuthor = this.primaryAuthor.toModel(),
            content = this.content,
            summary = this.summary,
            tags = tags,
            metadata = this.metadata.map { it.toModel() }.toSet(),
        )
}
