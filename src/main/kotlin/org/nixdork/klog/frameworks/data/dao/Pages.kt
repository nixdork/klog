package org.nixdork.klog.frameworks.data.dao

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import java.util.UUID

object Pages : UUIDTable(name = "page") {
    val title = text("title")
    val path = text("path")
    val linkId = uuid("link_id")
    val updated = timestamp("updated")
    val published = timestamp("published").nullable()
    val primaryAuthor = uuid("primary_author")
    val content = text("content")
    val rights = text("rights").nullable()
}

class Page(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<Page>(Pages)

    var title by Pages.title
    var path by Pages.path
    var linkId by Pages.linkId
    var updated by Pages.updated
    var published by Pages.published
    var primaryAuthor by Pages.primaryAuthor
    var content by Pages.content
    var rights by Pages.rights
}
