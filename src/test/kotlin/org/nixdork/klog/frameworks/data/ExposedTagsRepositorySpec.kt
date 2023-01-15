package org.nixdork.klog.frameworks.data

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.OffsetDateTime
import java.util.UUID
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.nixdork.klog.frameworks.data.dao.Tags
import org.nixdork.klog.util.createDatabaseTag
import org.nixdork.klog.util.createKotlinTag
import org.nixdork.klog.util.createRandomTag
import org.nixdork.klog.util.faker
import org.nixdork.klog.util.installPostgres

class ExposedTagsRepositorySpec : FunSpec({
    installPostgres()

    lateinit var tagsRepo: ExposedTagsRepository

    beforeEach {
        tagsRepo = ExposedTagsRepository()
    }

    context("GET") {
        (0..2).forEach {
            val tag = faker.createRandomTag()
            transaction {
                Tags.insert {
                    it[id] = tag.id
                    it[term] = tag.term
                    it[permalink] = tag.permalink
                    it[createdAt] = OffsetDateTime.now().toInstant()
                    it[updatedAt] = OffsetDateTime.now().toInstant()
                }
            }
        }
        val dbTag = faker.createDatabaseTag()
        transaction {
            Tags.insert {
                it[id] = dbTag.id
                it[term] = dbTag.term
                it[permalink] = dbTag.permalink
                it[createdAt] = OffsetDateTime.now().toInstant()
                it[updatedAt] = OffsetDateTime.now().toInstant()
            }
        }

        test("get all tags returns all tags") {
            val tags = tagsRepo.getAllTags()
            tags.tags.count() shouldBeExactly 4
        }

        test("get by valid term returns successfully") {
            val tag = tagsRepo.getTagByTerm("database")
            tag?.term shouldBe "database"
            tag?.entries?.count()?.shouldBeExactly(0)
        }

        test("get by invalid term returns null") {
            tagsRepo.getTagByTerm("random") shouldBe null
        }

        test("get by valid id returns successfully") {
            val tag = tagsRepo.getTagById(dbTag.id)
            tag?.term shouldBe "database"
            tag?.entries?.count()?.shouldBeExactly(0)
        }

        test("get by invalid id returns null") {
            tagsRepo.getTagById(UUID.randomUUID()) shouldBe null
        }
    }

    context("UPSERT") {
        test("update on pk conflict or insert otherwise") {
            val ktTag = faker.createKotlinTag()
            val inserted = tagsRepo.upsertTag(ktTag)
            val updated = tagsRepo.upsertTag(ktTag.copy(permalink = "gorauskas.org"))
            val current = tagsRepo.getTagByTerm("kotlin")!!
            inserted.id shouldBe ktTag.id
            inserted.term shouldBe ktTag.term
            inserted.permalink shouldBe ktTag.permalink
            updated.id shouldBe current.id
            updated.term shouldBe current.term
            updated.permalink shouldBe current.permalink
            // current.createdAt shouldBe inserted.createdAt
            current.updatedAt shouldNotBe null
        }
    }

    context("DELETE") {

    }
})

//     override fun upsertTag(tag: TagModel): TagModel =
//        transaction {
//            Tags.upsert {
//                it[id] = tag.id
//                it[term] = tag.term
//                it[permalink] = tag.permalink
//            }.resultedValues!!
//                .single()
//                .let {
//                    Tag.wrapRow(it).toModel()
//                }
//        }

// object Tags : UUIDTable("tag") {
//    val term = text("term")
//    val permalink = text("permalink")
//    val createdAt = timestamp("created_at")
//    val updatedAt = timestamp("updated_at").nullable()
//}

// interface TagsRepository {
//    fun getAllTags(): TagWrapperModel
//    fun getTagByTerm(term: String): TagModel?
//    fun getTagById(id: UUID): TagModel?
//
//    fun upsertTag(tag: TagModel): TagModel
//
//    fun deleteTag(tagId: UUID)
// }