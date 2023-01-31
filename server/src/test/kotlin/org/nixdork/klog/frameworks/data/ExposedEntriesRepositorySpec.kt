package org.nixdork.klog.frameworks.data

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.nixdork.klog.adapters.model.EntryModel
import org.nixdork.klog.adapters.model.PasswordCreateResetModel
import org.nixdork.klog.adapters.model.PersonModel
import org.nixdork.klog.adapters.model.TagModel
import org.nixdork.klog.common.generateHash
import org.nixdork.klog.common.generateSalt
import org.nixdork.klog.frameworks.data.dao.EntriesMetadata
import org.nixdork.klog.frameworks.data.dao.EntriesToTags
import org.nixdork.klog.frameworks.data.dao.EntryMetadata
import org.nixdork.klog.frameworks.data.dao.People
import org.nixdork.klog.frameworks.data.dao.Person
import org.nixdork.klog.frameworks.data.dao.Tag
import org.nixdork.klog.frameworks.data.dao.Tags
import org.nixdork.klog.util.createAdmin
import org.nixdork.klog.util.createDatabaseTag
import org.nixdork.klog.util.createKotlinTag
import org.nixdork.klog.util.createPassword
import org.nixdork.klog.util.createRandomAuthor
import org.nixdork.klog.util.createRandomEntry
import org.nixdork.klog.util.createRandomMetadata
import org.nixdork.klog.util.createRandomPassword
import org.nixdork.klog.util.faker
import org.nixdork.klog.util.insertEntry
import org.nixdork.klog.util.insertPerson
import org.nixdork.klog.util.insertTag
import org.nixdork.klog.util.installPostgres
import java.time.OffsetDateTime
import java.util.UUID

class ExposedEntriesRepositorySpec : FunSpec({
    installPostgres()

    lateinit var entriesRepo: ExposedEntriesRepository

    lateinit var dbTag: TagModel
    lateinit var ktTag: TagModel
    lateinit var tagList: Set<TagModel>

    lateinit var salt: String
    lateinit var hash: String
    lateinit var adminId: UUID
    lateinit var meAdmin: PersonModel
    lateinit var mePwd: PasswordCreateResetModel
    lateinit var authorId: UUID
    lateinit var authorEmail: String
    lateinit var anyAuthor: PersonModel
    lateinit var anyPwdAuthor: PasswordCreateResetModel

    lateinit var entry01: EntryModel
    lateinit var entry02: EntryModel
    lateinit var entry03: EntryModel
    lateinit var entry04: EntryModel

    beforeSpec {
        dbTag = insertTag(faker.createDatabaseTag())
        ktTag = insertTag(faker.createKotlinTag())
        tagList = setOf(dbTag, ktTag)

        adminId = UUID.randomUUID()
        mePwd = faker.createPassword(adminId)
        salt = generateSalt(16)
        hash = generateHash(mePwd.newPassword, salt)
        meAdmin = insertPerson(faker.createAdmin(adminId), salt, hash)

        authorId = UUID.randomUUID()
        authorEmail = faker.internet.email()
        anyPwdAuthor = faker.createRandomPassword(authorId, authorEmail)
        salt = generateSalt(16)
        hash = generateHash(anyPwdAuthor.newPassword, salt)
        anyAuthor = insertPerson(faker.createRandomAuthor(authorId, authorEmail), salt, hash)

        entriesRepo = ExposedEntriesRepository()
        entry01 = insertEntry(faker.createRandomEntry(meAdmin, setOf(dbTag)))
        entry02 = insertEntry(faker.createRandomEntry(meAdmin, setOf(ktTag)).copy(draft = true))
        entry03 = insertEntry(faker.createRandomEntry(meAdmin, tagList))
        entry04 = insertEntry(faker.createRandomEntry(anyAuthor, setOf(dbTag)).copy(draft = true))
    }

    context("GET") {
        test("get n entries return n or less entries") {
            val wrapper = entriesRepo.getLastestNEntries(2)
            wrapper.entries.count() shouldBeExactly 2
        }

        test("get all returns all entries") {
            val wrapper = entriesRepo.getAllEntries()
            wrapper.entries.count() shouldBeExactly 4
        }

        test("get by tag returns all entries for the tag") {
            val tagToEntries = entriesRepo.getEntriesByTag(ktTag.term)
            tagToEntries.tag.term shouldBe ktTag.term
            tagToEntries.entries.count() shouldBe 2
        }

        test("get archived entries returns entries grouped by date") {
            val archived = entriesRepo.getArchivedEntries()
            val ym = "${OffsetDateTime.now().year}-${OffsetDateTime.now().month.value.toString().padStart(2, '0')}"
            archived.archives.count() shouldBe 1
            archived.archives.single().entries.count() shouldBe 2
            archived.archives.single().monthYear shouldBe ym
        }

        test("given existing id return entry") {
            val entry = entriesRepo.getEntryById(entry03.id)
            entry?.slug shouldBe entry03.slug
            entry?.draft shouldBe false
            entry?.primaryAuthor?.email shouldBe meAdmin.email
            entry?.tags?.count() shouldBe 2
            entry?.tags?.first()?.term shouldBe dbTag.term
        }

        test("given an id that does not exist return null") {
            entriesRepo.getEntryById(UUID.randomUUID()) shouldBe null
        }

        test("given existing slug return entry") {
            val entry = entriesRepo.getEntryBySlug(entry04.slug!!)
            entry?.id shouldBe entry04.id
            entry?.draft shouldBe true
            entry?.primaryAuthor?.email shouldBe anyAuthor.email
            entry?.tags?.count() shouldBe 1
            entry?.tags?.single()?.term shouldBe dbTag.term
        }

        test("given a slug that does not exist return null") {
            entriesRepo.getEntryBySlug("slug-me") shouldBe null
        }
    }

    context("UPSERT") {
        test("update entry on pk conflict or insert otherwise") {
            val before = entriesRepo.getEntryBySlug(entry04.slug!!)!!
            val updated = entriesRepo.upsertEntry(entry04.primaryAuthor.id, entry04.copy(draft = false, tags = tagList))
            before.id shouldBe updated.id
            before.slug shouldBe updated.slug
            before.draft shouldBe true
            updated.draft shouldBe false
            before.tags shouldBe listOf(dbTag)
            updated.tags.forEach { tag ->
                when (tag.term) {
                    dbTag.term -> {
                        tag.updatedAt?.shouldBeGreaterThan(dbTag.updatedAt ?: OffsetDateTime.MIN)
                    }
                    ktTag.term -> {
                        tag.updatedAt?.shouldBeGreaterThan(ktTag.updatedAt ?: OffsetDateTime.MIN)
                    }
                    else -> Unit
                }
            }
        }

        test("publish entry sets draft to false and published at date") {
            val before = entriesRepo.getEntryById(entry02.id)!!
            val updated = entriesRepo.publishEntry(entry02.id)
            before.id shouldBe updated.id
            before.slug shouldBe updated.slug
            before.draft shouldBe true
            updated.draft shouldBe false
            updated.publishedAt?.shouldBeGreaterThan(before.publishedAt!!)
        }

        test("update metadata given entry id") {
            val before = entriesRepo.getEntryById(entry01.id)!!
            entriesRepo.batchUpsertMetadata(entry01.id, faker.createRandomMetadata(2, entry01.id))
            val updated = entriesRepo.getEntryById(entry01.id)!!
            before.id shouldBe updated.id
            before.slug shouldBe updated.slug
            before.metadata shouldNotBe updated.metadata
        }
    }

    context("DELETE") {
        test("delete entry given an existing id") {
            val before = entriesRepo.getEntryById(entry03.id)
            val beforeTags = transaction {
                Tags.innerJoin(EntriesToTags)
                    .select { EntriesToTags.entryId eq entry03.id }
                    .map { Tag.wrapRow(it).toModel() }
                    .toSet()
            }
            val beforeMetadata = transaction {
                EntriesMetadata.select { EntriesMetadata.entryId eq entry03.id }
                    .map { EntryMetadata.wrapRow(it).toModel() }
                    .toSet()
            }
            val beforeAuthor = transaction {
                People.select { People.id eq entry03.primaryAuthor.id }.firstOrNull()
                    ?.let { Person.wrapRow(it).toModel() }
            }
            before shouldNotBe null
            beforeTags.count() shouldBe 2
            beforeMetadata.count() shouldBe 2
            beforeAuthor?.name shouldBe meAdmin.name

            entriesRepo.deleteEntryById(entry03.id)
            val after = entriesRepo.getEntryById(entry03.id)
            val afterTags = transaction {
                Tags.innerJoin(EntriesToTags)
                    .select { EntriesToTags.entryId eq entry03.id }
                    .map { Tag.wrapRow(it).toModel() }
                    .toSet()
            }
            val afterMetadata = transaction {
                EntriesMetadata.select { EntriesMetadata.entryId eq entry03.id }
                    .map { EntryMetadata.wrapRow(it).toModel() }
                    .toSet()
            }
            val afterAuthor = transaction {
                People.select { People.id eq entry03.primaryAuthor.id }.firstOrNull()
                    ?.let { Person.wrapRow(it).toModel() }
            }
            after shouldBe null
            afterTags.count() shouldBe 0
            afterMetadata.count() shouldBe 0
            afterAuthor?.name shouldBe meAdmin.name
        }

        test("do nothing given an id that does not exist") {
            val uuid = UUID.randomUUID()
            val before = entriesRepo.getEntryById(uuid)
            entriesRepo.deleteEntryById(uuid)
            val after = entriesRepo.getEntryById(uuid)
            before shouldBe null
            after shouldBe null
        }

        test("delete entry given an existing slug") {
            val before = entriesRepo.getEntryBySlug(entry04.slug!!)
            val beforeTags = transaction {
                Tags.innerJoin(EntriesToTags)
                    .select { EntriesToTags.entryId eq entry04.id }
                    .map { Tag.wrapRow(it).toModel() }
                    .toSet()
            }
            val beforeMetadata = transaction {
                EntriesMetadata.select { EntriesMetadata.entryId eq entry04.id }
                    .map { EntryMetadata.wrapRow(it).toModel() }
                    .toSet()
            }
            val beforeAuthor = transaction {
                People.select { People.id eq entry04.primaryAuthor.id }.firstOrNull()
                    ?.let { Person.wrapRow(it).toModel() }
            }
            before shouldNotBe null
            beforeTags.count() shouldBe 2
            beforeMetadata.count() shouldBe 2
            beforeAuthor?.name shouldBe anyAuthor.name

            entriesRepo.deleteEntryBySlug(entry04.slug!!)
            val after = entriesRepo.getEntryBySlug(entry04.slug!!)
            val afterTags = transaction {
                Tags.innerJoin(EntriesToTags)
                    .select { EntriesToTags.entryId eq entry04.id }
                    .map { Tag.wrapRow(it).toModel() }
                    .toSet()
            }
            val afterMetadata = transaction {
                EntriesMetadata.select { EntriesMetadata.entryId eq entry04.id }
                    .map { EntryMetadata.wrapRow(it).toModel() }
                    .toSet()
            }
            val afterAuthor = transaction {
                People.select { People.id eq entry04.primaryAuthor.id }.firstOrNull()
                    ?.let { Person.wrapRow(it).toModel() }
            }
            after shouldBe null
            afterTags.count() shouldBe 0
            afterMetadata.count() shouldBe 0
            afterAuthor?.name shouldBe anyAuthor.name
        }

        test("do nothing given a slug that does not exist") {
            val missingSlug = "missing-slug"
            val before = entriesRepo.getEntryBySlug(missingSlug)
            entriesRepo.deleteEntryBySlug(missingSlug)
            val after = entriesRepo.getEntryBySlug(missingSlug)
            before shouldBe null
            after shouldBe null
        }
    }
})
