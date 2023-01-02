package org.nixdork.klog.frameworks.data.dao

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import org.nixdork.klog.adapters.model.TagModel
import org.nixdork.klog.common.toOffsetDateTime
import java.util.UUID

object Tags : UUIDTable("tag") {
    val term = text("term")
    val permalink = text("permalink")
    val createdAt = timestamp("created_at")
    val updatedAt = timestamp("updated_at").nullable()
}

class Tag(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<Tag>(Tags)

    var term by Tags.term
    var permalink by Tags.permalink
    var createdAt by Tags.createdAt
    var updatedAt by Tags.updatedAt

    var entries by Entry via EntriesToTags

    fun toModel(): TagModel =
        TagModel(
            id = this.id.value,
            term = this.term,
            permalink = this.permalink,
            entries = this.entries.map { it.toModel() },
            createdAt = this.createdAt.toOffsetDateTime(),
            updatedAt = this.updatedAt?.toOffsetDateTime()
        )
}
