package org.nixdork.klog.frameworks.data.dao

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.nixdork.klog.adapters.model.TagModel
import org.nixdork.klog.common.CurrentOffsetDateTime
import org.nixdork.klog.common.offsetDateTime
import java.util.UUID

object Tags : UUIDTable("tag") {
    val term = text("term")
    val permalink = text("permalink")
    val createdAt = offsetDateTime("created_at").defaultExpression(CurrentOffsetDateTime())
    val updatedAt = offsetDateTime("updated_at").nullable()
}

class Tag(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Tag>(Tags)

    var term by Tags.term
    var permalink by Tags.permalink
    var createdAt by Tags.createdAt
    var updatedAt by Tags.updatedAt

    fun toModel(): TagModel =
        TagModel(
            id = this.id.value,
            term = this.term,
            permalink = this.permalink,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
        )
}
