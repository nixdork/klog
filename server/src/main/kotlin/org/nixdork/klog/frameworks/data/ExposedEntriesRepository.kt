package org.nixdork.klog.frameworks.data

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchReplace
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.nixdork.klog.adapters.data.EntriesRepository
import org.nixdork.klog.adapters.model.ArchiveModel
import org.nixdork.klog.adapters.model.ArchiveWrapperModel
import org.nixdork.klog.adapters.model.EntryMetadataModel
import org.nixdork.klog.adapters.model.EntryModel
import org.nixdork.klog.adapters.model.EntryWrapperModel
import org.nixdork.klog.adapters.model.TagToEntriesWrapperModel
import org.nixdork.klog.common.upsert
import org.nixdork.klog.frameworks.data.dao.Entries
import org.nixdork.klog.frameworks.data.dao.EntriesMetadata
import org.nixdork.klog.frameworks.data.dao.EntriesToTags
import org.nixdork.klog.frameworks.data.dao.Entry
import org.nixdork.klog.frameworks.data.dao.Tag
import org.nixdork.klog.frameworks.data.dao.Tags
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@Component
class ExposedEntriesRepository : EntriesRepository {
    override fun getLastestNEntries(n: Int): EntryWrapperModel =
        transaction {
            Entries.select { Entries.draft eq false }
                .orderBy(Entries.updatedAt to SortOrder.DESC)
                .limit(n)
                .map { entry ->
                    Entry.wrapRow(entry)
                        .toModel(
                            Tags.innerJoin(EntriesToTags)
                                .select { EntriesToTags.entryId eq entry[Entries.id] }
                                .map { tag -> Tag.wrapRow(tag).toModel() }
                                .toSet(),
                        )
                }
        }.toEntryWrapper()

    override fun getAllEntries(): EntryWrapperModel =
        transaction {
            Entry.all()
                .map { entry ->
                    entry.toModel(
                        Tags.innerJoin(EntriesToTags)
                            .select { EntriesToTags.entryId eq entry.id }
                            .map { tag -> Tag.wrapRow(tag).toModel() }
                            .toSet(),
                    )
                }.toEntryWrapper()
        }

    override fun getEntryBySlug(slug: String): EntryModel? =
        transaction {
            Entry.find(Entries.slug eq slug)
                .singleOrNull()
                ?.let { entry ->
                    entry.toModel(
                        Tags.innerJoin(EntriesToTags)
                            .select { EntriesToTags.entryId eq entry.id }
                            .map { tag -> Tag.wrapRow(tag).toModel() }
                            .toSet(),
                    )
                }
        }

    override fun getEntryById(id: UUID): EntryModel? =
        transaction {
            Entry.findById(id)?.let { entry ->
                entry.toModel(
                    Tags.innerJoin(EntriesToTags)
                        .select { EntriesToTags.entryId eq entry.id }
                        .map { tag -> Tag.wrapRow(tag).toModel() }
                        .toSet(),
                )
            }
        }

    override fun getEntriesByTag(term: String): TagToEntriesWrapperModel =
        transaction {
            val tag = Tag.find(Tags.term eq term).singleOrNull()?.toModel()
            requireNotNull(tag) { "Tag not found!" }
            val entries = Entries.innerJoin(EntriesToTags)
                .select(EntriesToTags.tagId eq tag.id)
                .map { Entry.wrapRow(it) }
                .let { entries ->
                    entries.map { entry ->
                        entry.toModel(
                            Tags.innerJoin(EntriesToTags)
                                .select { EntriesToTags.entryId eq entry.id }
                                .map { tag -> Tag.wrapRow(tag).toModel() }
                                .toSet(),
                        )
                    }
                }
            TagToEntriesWrapperModel(
                tag = tag,
                entries = entries,
            )
        }

    override fun getArchivedEntries(): ArchiveWrapperModel =
        transaction {
            Entries.select {
                Entries.draft eq false
            }.groupBy {
                val year = it[Entries.publishedAt]?.year?.toString()?.padStart(4, '0')
                val month = it[Entries.publishedAt]?.month?.value?.toString()?.padStart(2, '0')
                "$year-$month"
            }.map {
                val (year, month) = it.key.split('-')
                ArchiveModel(
                    date = LocalDate.of(year.toInt(), month.toInt(), 1),
                    entries = it.value.map { Entry.wrapRow(it) }.let { entries ->
                        entries.map { entry ->
                            entry.toModel(
                                Tags.innerJoin(EntriesToTags)
                                    .select { EntriesToTags.entryId eq entry.id }
                                    .map { tag -> Tag.wrapRow(tag).toModel() }
                                    .toSet(),
                            )
                        }
                    },
                )
            }.let { ArchiveWrapperModel(archives = it) }
        }

    override fun upsertEntry(authorId: UUID, entry: EntryModel): EntryModel =
        transaction {
            Entries.upsert {
                it[id] = entry.id
                it[slug] = entry.slug!!
                it[permalink] = entry.permalink
                it[title] = entry.title
                it[draft] = entry.draft
                it[primaryAuthor] = authorId
                it[content] = entry.content
                it[summary] = entry.summary
            }.resultedValues!!
                .single()
                .also {
                    if (entry.metadata != null) {
                        if (entry.metadata.isNotEmpty()) {
                            batchUpsertMetadata(entry.id, entry.metadata)
                        }
                        if (entry.tags.isNotEmpty()) {
                            entry.tags.forEach { tag ->
                                Tags.upsert {
                                    it[id] = tag.id
                                    it[term] = tag.term
                                    it[permalink] = tag.permalink
                                }
                                if (EntriesToTags.select {
                                        (EntriesToTags.entryId eq entry.id) and (EntriesToTags.tagId eq tag.id)
                                    }.count().toInt() == 0
                                ) {
                                    EntriesToTags.insert {
                                        it[entryId] = entry.id
                                        it[tagId] = tag.id
                                    }
                                }
                            }
                        }
                    }
                }
                .let { row ->
                    Entry.wrapRow(row)
                        .toModel(
                            Tags.innerJoin(EntriesToTags)
                                .select { EntriesToTags.entryId eq row[Entries.id] }
                                .map { tag -> Tag.wrapRow(tag).toModel() }
                                .toSet(),
                        )
                }
        }

    override fun publishEntry(entryId: UUID): EntryModel =
        transaction {
            requireNotNull(Entry.findById(entryId)) {
                "Entry with ID $entryId not found!"
            }
            Entries.update({ Entries.id eq entryId }) {
                it[draft] = false
                it[publishedAt] = OffsetDateTime.now()
            }
            Entry.findById(entryId)!!.toModel(
                Tags.innerJoin(EntriesToTags)
                    .select { EntriesToTags.entryId eq entryId }
                    .map { tag -> Tag.wrapRow(tag).toModel() }
                    .toSet(),
            )
        }

    override fun batchUpsertMetadata(entryId: UUID, metadata: Set<EntryMetadataModel>) {
        transaction {
            EntriesMetadata.batchReplace(metadata) {
                this[EntriesMetadata.id] = it.id
                this[EntriesMetadata.key] = it.key
                this[EntriesMetadata.value] = it.value
                this[EntriesMetadata.entryId] = entryId
            }
        }
    }

    override fun deleteEntryById(entryId: UUID) {
        transaction {
            EntriesToTags.deleteWhere { EntriesToTags.entryId eq entryId }
            Entries.deleteWhere { Entries.id eq entryId }
        }
    }

    override fun deleteEntryBySlug(slug: String) {
        transaction {
            val entryId = Entry.find { Entries.slug eq slug }.singleOrNull()?.id?.value ?: return@transaction
            deleteEntryById(entryId)
        }
    }

    private fun List<EntryModel>.toEntryWrapper(): EntryWrapperModel = EntryWrapperModel(entries = this)
}
