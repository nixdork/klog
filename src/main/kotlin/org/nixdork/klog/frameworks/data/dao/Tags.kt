package org.nixdork.klog.frameworks.data.dao

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID
import org.nixdork.klog.adapters.model.TagModel

object Tags : UUIDTable("tag") {
    val term = text("term")
    val permalink = text("permalink")
    val label = text("label").nullable()
    val scheme = text("scheme").nullable()
}

class Tag(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<Tag>(Tags)

    var term by Tags.term
    var permalink by Tags.permalink
    var label by Tags.label
    var scheme by Tags.scheme

    fun toModel(): TagModel =
        TagModel(
            id = this.id.value,
            term = this.term,
            permalink = this.permalink,
            label = this.label,
            scheme = this.scheme
        )
}
