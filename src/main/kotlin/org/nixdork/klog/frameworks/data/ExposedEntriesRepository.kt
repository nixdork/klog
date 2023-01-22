package org.nixdork.klog.frameworks.data

import org.nixdork.klog.adapters.data.EntriesRepository
import org.nixdork.klog.adapters.model.ArchiveWrapperModel
import org.nixdork.klog.adapters.model.EntryMetadataModel
import org.nixdork.klog.adapters.model.EntryModel
import org.nixdork.klog.adapters.model.EntryWrapperModel
import org.nixdork.klog.adapters.model.TagToEntriesWrapperModel
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.batchReplace
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.nixdork.klog.adapters.model.ArchiveModel
import org.nixdork.klog.common.toOffsetDateTime
import org.nixdork.klog.common.upsert
import org.nixdork.klog.frameworks.data.dao.Entries
import org.nixdork.klog.frameworks.data.dao.EntriesMetadata
import org.nixdork.klog.frameworks.data.dao.EntriesToTags
import org.nixdork.klog.frameworks.data.dao.Entry
import org.nixdork.klog.frameworks.data.dao.Tag
import org.nixdork.klog.frameworks.data.dao.Tags
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Component
class ExposedEntriesRepository : EntriesRepository {
    override fun getLastestNEntries(n: Int): EntryWrapperModel =
        transaction {
            Entries.select { Entries.draft eq false }
                .orderBy(Entries.updatedAt to SortOrder.DESC)
                .limit(n)
                .map {
                    Entry.wrapRow(it).toModel()
                }
        }.toEntryWrapper()

    override fun getAllEntries(): EntryWrapperModel =
        transaction { Entry.all().map { it.toModel() } }.toEntryWrapper()

    override fun getEntryBySlug(slug: String): EntryModel? =
        transaction { Entry.find(Entries.slug eq slug).singleOrNull()?.toModel() }

    override fun getEntryById(id: UUID): EntryModel? = transaction { Entry.findById(id)?.toModel() }

    override fun getEntriesByTag(term: String): TagToEntriesWrapperModel =
        transaction {
            (Tags innerJoin EntriesToTags innerJoin Entries)
                .select { Tags.term eq term }
                .map { Pair(Tag.wrapRow(it), Entry.wrapRow(it)) }
                .groupBy { it.first }
                .let {
                    TagToEntriesWrapperModel(
                        tag = it.keys.first().toModel(),
                        entries = it.values.flatMap { it.map { p -> p.second.toModel() } }
                    )
                }
        }

    override fun getArchivedEntries(): ArchiveWrapperModel =
        transaction {
            Entries.select {
                Entries.draft eq false
            }.groupBy {
                val year = it[Entries.publishedAt]?.toOffsetDateTime()?.year?.toString()?.padStart(4, '0')
                val month = it[Entries.publishedAt]?.toOffsetDateTime()?.month?.toString()?.padStart(2, '0')
                "$year-$month"
            }.map {
                val (year, month) = it.key.split('-')
                ArchiveModel(
                    date = LocalDate.of(year.toInt(), month.toInt(), 1),
                    entries = it.value.map { entry -> Entry.wrapRow(entry).toModel() }
                )
            }.let { ArchiveWrapperModel(archives = it) }
        }

    override fun upsertEntry(authorId: UUID, entry: EntryModel): EntryModel =
        transaction {
            Entries.upsert {
                it[id] = entry.id
                it[slug] = entry.slug
                it[permalink] = entry.permalink
                it[title] = entry.title
                it[draft] = entry.draft
                it[primaryAuthor] = authorId
                it[content] = entry.content
                it[summary] = entry.summary
            }.resultedValues!!
                .single()
                .let {
                    Entry.wrapRow(it).toModel()
                }
        }

    override fun publishEntry(entryId: UUID): EntryModel =
        transaction {
            Entries.upsert {
                it[id] = entryId
                it[draft] = false
                it[publishedAt] = Instant.now()
            }.resultedValues!!
                .single()
                .let {
                    Entry.wrapRow(it).toModel()
                }
        }

    override fun batchUpsertMetadata(entryId: UUID, metadata: List<EntryMetadataModel>) {
        EntriesMetadata.batchReplace(metadata) {
            this[EntriesMetadata.id] = it.id
            this[EntriesMetadata.key] = it.key
            this[EntriesMetadata.value] = it.value
            this[EntriesMetadata.entry] = entryId
        }
    }

    override fun deleteEntry(entryId: UUID) {
        transaction { Entries.deleteWhere { Entries.id eq entryId } }
    }

    private fun List<EntryModel>.toEntryWrapper(): EntryWrapperModel = EntryWrapperModel(entries = this)
}