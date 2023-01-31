package org.nixdork.klog.frameworks.data.dao

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.nixdork.klog.adapters.model.EntryMetadataModel
import org.nixdork.klog.common.CurrentOffsetDateTime
import org.nixdork.klog.common.offsetDateTime
import org.nixdork.klog.frameworks.data.dao.EntriesToTags.references
import java.util.UUID

object EntriesMetadata : UUIDTable("entry_metadata") {
    val entryId = reference("entry_id", Entries)
    val key = text("key")
    val value = text("value")
    val createdAt = offsetDateTime("created_at").defaultExpression(CurrentOffsetDateTime())
    val updatedAt = offsetDateTime("updated_at").nullable()
}

class EntryMetadata(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<EntryMetadata>(EntriesMetadata)

    var entryId by EntriesMetadata.entryId references Entries.id
    var key by EntriesMetadata.key
    var value by EntriesMetadata.value
    var createdAt by EntriesMetadata.createdAt
    var updatedAt by EntriesMetadata.updatedAt

    fun toModel(): EntryMetadataModel =
        EntryMetadataModel(
            id = this.id.value,
            entryId = this.entryId.value,
            key = this.key,
            value = this.value,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
        )
}
