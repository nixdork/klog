package org.nixdork.klog.frameworks.data.dao

import org.jetbrains.exposed.dao.id.UUIDTable

object EntriesToTags : UUIDTable("entry_to_tag") {
    val entry = reference("entry_id", Entries)
    val tag = reference("tag_id", Tags)
}
