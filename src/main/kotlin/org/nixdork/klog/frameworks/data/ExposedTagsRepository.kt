package org.nixdork.klog.frameworks.data

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.nixdork.klog.adapters.data.TagsRepository
import org.nixdork.klog.adapters.model.TagModel
import org.nixdork.klog.adapters.model.TagWrapperModel
import org.nixdork.klog.common.upsert
import org.nixdork.klog.frameworks.data.dao.Tag
import org.nixdork.klog.frameworks.data.dao.Tags
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ExposedTagsRepository : TagsRepository {
    override fun getAllTags(): TagWrapperModel =
        transaction { TagWrapperModel(Tag.all().map { it.toModel() }) }

    override fun getTagByTerm(term: String): TagModel? =
        transaction { Tag.find(Tags.term eq term).singleOrNull()?.toModel() }

    override fun getTagById(id: UUID): TagModel? =
        transaction { Tag.findById(id)?.toModel() }

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

    override fun deleteTagById(tagId: UUID) {
        transaction { Tags.deleteWhere { Tags.id eq tagId } }
    }

    override fun deleteTagByTerm(term: String) {
        transaction { Tags.deleteWhere { Tags.term eq term } }
    }
}
