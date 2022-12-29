package org.nixdork.klog.frameworks.data.dao

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.UUID
import org.nixdork.klog.adapters.model.LinkModel

object Links : UUIDTable("link") {
    val href = text("href")
    val title = text("title").nullable()
    val rel = text("rel").nullable()
    val type = text("type").nullable()
    val hreflang = text("hreflang").nullable()
    val length = long("length").nullable()
}

class Link(id: EntityID<UUID>): UUIDEntity(id) {
    companion object : UUIDEntityClass<Link>(Links)

    var href by Links.href
    var title by Links.title
    var rel by Links.rel
    var type by Links.type
    var hreflang by Links.hreflang
    var length by Links.length

    fun toModel(): LinkModel =
        LinkModel(
            id = this.id.value,
            href = this.href,
            title = this.title,
            rel = this.rel,
            type = this.type,
            hreflang = this.hreflang,
            length = this.length
        )
}
