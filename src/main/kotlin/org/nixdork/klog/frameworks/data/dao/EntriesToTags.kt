package org.nixdork.klog.frameworks.data.dao

import org.jetbrains.exposed.dao.id.UUIDTable

object EntryToTag : UUIDTable("entry_to_tag") {
    val entryId = reference("entry_id", Entries)
    val tagId = reference("tag_id", Tags)
}
