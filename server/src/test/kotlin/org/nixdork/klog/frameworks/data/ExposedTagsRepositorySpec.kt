package org.nixdork.klog.frameworks.data

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.date.shouldHaveSameDayAs
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.nixdork.klog.adapters.model.TagModel
import org.nixdork.klog.util.createDatabaseTag
import org.nixdork.klog.util.createKotlinTag
import org.nixdork.klog.util.createRandomTag
import org.nixdork.klog.util.faker
import org.nixdork.klog.util.insertTag
import org.nixdork.klog.util.installPostgres
import java.util.UUID

class ExposedTagsRepositorySpec : FunSpec({
    installPostgres()

    lateinit var tagsRepo: ExposedTagsRepository
    lateinit var r1Tag: TagModel
    lateinit var r2Tag: TagModel
    lateinit var dbTag: TagModel
    lateinit var ktTag: TagModel

    beforeSpec {
        tagsRepo = ExposedTagsRepository()
        (0..2).forEach {
            val tag = faker.createRandomTag()
            insertTag(tag)
            if (it == 1) r1Tag = tag
            if (it == 2) r2Tag = tag
        }
        dbTag = faker.createDatabaseTag()
        insertTag(dbTag)
        ktTag = faker.createKotlinTag()
        insertTag(ktTag)
    }

    context("GET") {
        test("get all returns all tags") {
            val tags = tagsRepo.getAllTags()
            tags.tags.count() shouldBeExactly 5
        }

        test("given existing term return tag") {
            val tag = tagsRepo.getTagByTerm("database")
            tag?.term shouldBe "database"
        }

        test("given a term that does not exist return null") {
            tagsRepo.getTagByTerm("random") shouldBe null
        }

        test("given existing id return tag") {
            val tag = tagsRepo.getTagById(r1Tag.id)
            tag?.term shouldBe r1Tag.term
        }

        test("given an id that does not exist return null") {
            tagsRepo.getTagById(UUID.randomUUID()) shouldBe null
        }
    }

    context("UPSERT") {
        test("update on pk conflict or insert otherwise") {
            val updated = tagsRepo.upsertTag(ktTag.copy(permalink = "gorauskas.org"))
            val current = tagsRepo.getTagByTerm("kotlin")!!
            updated.id shouldBe current.id
            updated.term shouldBe current.term
            updated.permalink shouldBe current.permalink
            current.createdAt?.shouldHaveSameDayAs(updated.createdAt!!)
            current.updatedAt shouldNotBe null
        }
    }

    context("DELETE") {
        test("delete tag given an existing id") {
            tagsRepo.deleteTagById(r1Tag.id)
            tagsRepo.getTagById(r1Tag.id) shouldBe null
        }

        test("do nothing given an id that does not exist") {
            val id = UUID.randomUUID()
            tagsRepo.getTagById(id) shouldBe null
            tagsRepo.deleteTagById(id)
            tagsRepo.getTagById(id) shouldBe null
        }

        test("delete tag given an existing term") {
            tagsRepo.deleteTagByTerm(r2Tag.term)
            tagsRepo.getTagByTerm(r2Tag.term) shouldBe null
        }

        test("do nothing given a term that does not exist") {
            val rt = faker.createRandomTag()
            tagsRepo.getTagByTerm(rt.term) shouldBe null
            tagsRepo.deleteTagByTerm(rt.term)
            tagsRepo.getTagByTerm(rt.term) shouldBe null
        }
    }
})
