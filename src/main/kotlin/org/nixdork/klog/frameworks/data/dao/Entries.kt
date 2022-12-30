package org.nixdork.klog.frameworks.data.dao

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import java.util.UUID

object Entries : UUIDTable(name = "entry") {
    val slug = text("slug")
    val title = text("title")
    val permalink = text("permalink")
    val draft = bool("draft")
    val updated = timestamp("updated")
    val published = timestamp("published").nullable()
    val primaryAuthor = uuid("primary_author")
    val content = text("content")
    val summary = text("summary").nullable()
    val rights = text("rights").nullable()
}

class Entry(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<Entry>(Entries)

    var slug by Entries.slug
    var title by Entries.title
    var permalink by Entries.permalink
    var draft by Entries.draft
    var updated by Entries.updated
    var published by Entries.published
    var primaryAuthor by Entries.primaryAuthor
    var content by Entries.content
    var summary by Entries.summary
    var rights by Entries.rights
}
