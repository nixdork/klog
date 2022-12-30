package org.nixdork.klog.frameworks.data.dao

import org.jetbrains.exposed.sql.Table

object EntryToLink : Table("entry_to_link") {
    val entryId = reference("entry_id", Entries)
    val linkId = reference("link_id", Links)
    override val primaryKey = PrimaryKey(entryId, linkId)
}

object EntryToTag : Table("entry_to_tag") {
    val entryId = reference("entry_id", Entries)
    val tagId = reference("tag_id", Tags)
    override val primaryKey = PrimaryKey(entryId, tagId)
}

object EntryToContributor : Table("entry_to_contributor") {
    val entryId = reference("entry_id", Entries)
    val personId = reference("person_id", People)
    override val primaryKey = PrimaryKey(entryId, personId)
}

object EntryToSource : Table("entry_to_source") {
    val entryId = reference("entry_id", Entries)
    val sourceId = reference("source_id", Links)
    override val primaryKey = PrimaryKey(entryId, sourceId)
}

object PageToLink : Table("page_to_link") {
    val pageId = reference("page_id", Pages)
    val linkId = reference("link_id", Links)
    override val primaryKey = PrimaryKey(pageId, linkId)
}

object PageToContributor : Table("page_to_contributor") {
    val pageId = reference("page_id", Pages)
    val personId = reference("person_id", People)
    override val primaryKey = PrimaryKey(pageId, personId)
}
