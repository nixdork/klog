package org.nixdork.klog.frameworks.data.dao

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import org.nixdork.klog.adapters.model.EntryMetadataModel
import org.nixdork.klog.common.toOffsetDateTime
import org.nixdork.klog.frameworks.data.dao.EntriesToTags.references
import java.time.OffsetDateTime
import java.util.UUID

object EntriesMetadata : UUIDTable("entry_metadata") {
    val entryId = reference("entry_id", Entries)
    val key = text("key")
    val value = text("value")
    val createdAt = timestamp("created_at").default(OffsetDateTime.now().toInstant())
    val updatedAt = timestamp("updated_at").nullable()
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
            createdAt = this.createdAt.toOffsetDateTime(),
            updatedAt = this.updatedAt?.toOffsetDateTime(),
        )
}
