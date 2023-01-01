package org.nixdork.klog.frameworks.data

import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.batchReplace
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.nixdork.klog.adapters.data.KlogRepository
import org.nixdork.klog.adapters.model.ArchiveModel
import org.nixdork.klog.adapters.model.ArchiveWrapperModel
import org.nixdork.klog.adapters.model.EntryMetadataModel
import org.nixdork.klog.adapters.model.EntryModel
import org.nixdork.klog.adapters.model.EntryWrapperModel
import org.nixdork.klog.adapters.model.PersonModel
import org.nixdork.klog.adapters.model.PersonPasswordModel
import org.nixdork.klog.adapters.model.TagModel
import org.nixdork.klog.adapters.model.TagToEntriesWrapperModel
import org.nixdork.klog.adapters.model.TagWrapperModel
import org.nixdork.klog.common.toOffsetDateTime
import org.nixdork.klog.common.upsert
import org.nixdork.klog.frameworks.data.dao.Entries
import org.nixdork.klog.frameworks.data.dao.EntriesMetadata
import org.nixdork.klog.frameworks.data.dao.EntriesToTags
import org.nixdork.klog.frameworks.data.dao.Entry
import org.nixdork.klog.frameworks.data.dao.People
import org.nixdork.klog.frameworks.data.dao.Person
import org.nixdork.klog.frameworks.data.dao.Tag
import org.nixdork.klog.frameworks.data.dao.Tags
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Suppress("TooManyFunctions")
@Component
class ExposedKlogRepository : KlogRepository {
    override fun getLastestNEntries(n: Int): EntryWrapperModel =
        transaction {
            Entries.select { Entries.draft eq false }
                .orderBy(Entries.updatedAt to SortOrder.DESC)
                .limit(n)
                .map {
                    Entry.wrapRow(it).toModel() }
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
                    monthYear = it.key,
                    entries = it.value.map { entry -> Entry.wrapRow(entry).toModel() }
                )
            }.let { ArchiveWrapperModel(archives = it) }
        }

    override fun getAllPeople(): List<PersonModel> = transaction { Person.all().map { it.toModel() } }

    override fun getPersonByEmail(email: String): PersonModel? =
        transaction { Person.find(People.email eq email).singleOrNull()?.toModel() }

    override fun getPersonById(id: UUID): PersonModel? = transaction { Person.findById(id)?.toModel() }

    override fun getPasswordByEmail(email: String): PersonPasswordModel? =
        transaction { Person.find(People.email eq email).singleOrNull()?.toPasswordModel() }

    override fun verifyPassword(password: PersonPasswordModel): Boolean =
        transaction { password.hash == Person.find(People.email eq password.email).singleOrNull()?.hash }

    override fun getAllTags(): TagWrapperModel = transaction { TagWrapperModel(Tag.all().map { it.toModel() }) }

    override fun getTagByTerm(term: String): TagModel? =
        transaction { Tag.find(Tags.term eq term).singleOrNull()?.toModel() }

    override fun getTagById(id: UUID): TagModel? = transaction { Tag.findById(id)?.toModel() }

    override fun updateLastLogin(personId: UUID): PersonModel? =
        transaction {
            People.update({ People.id eq personId }) { it[lastLoginAt] = Instant.now() }
            Person.findById(personId)?.toModel()
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

    override fun upsertPerson(person: PersonModel): PersonModel =
        transaction {
            People.upsert {
                it[id] = person.id
                it[email] = person.email
                it[name] = person.name
                it[role] = person.role
                it[uri] = person.uri
                it[avatar] = person.avatar
            }.resultedValues!!
                .single()
                .let {
                    Person.wrapRow(it).toModel()
                }
        }

    override fun upsertPassword(password: PersonPasswordModel): PersonModel =
        transaction {
            People.upsert {
                it[id] = password.id
                it[email] = password.email
                it[hash] = password.hash
                it[salt] = password.salt
                it[role] = password.role
                it[pwat] = Instant.now()
            }.resultedValues!!
                .single()
                .let {
                    Person.wrapRow(it).toModel()
                }
        }

    override fun upsertTag(tag: TagModel): TagModel =
        transaction {
            Tags.upsert {
                it[id] = tag.id
                it[term] = tag.term
                it[permalink] = tag.permalink
            }.resultedValues!!
                .single()
                .let {
                    Tag.wrapRow(it).toModel()
                }
        }

    override fun deleteEntry(entryId: UUID) { transaction { Entries.deleteWhere { Entries.id eq entryId } } }

    override fun deletePerson(personId: UUID) { transaction { People.deleteWhere { People.id eq personId } } }

    override fun deleteTag(tagId: UUID) { transaction { Tags.deleteWhere { Tags.id eq tagId } } }

    private fun List<EntryModel>.toEntryWrapper(): EntryWrapperModel = EntryWrapperModel(entries = this)
}
