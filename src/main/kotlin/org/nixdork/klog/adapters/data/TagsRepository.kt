package org.nixdork.klog.adapters.data

import org.nixdork.klog.adapters.model.TagModel
import org.nixdork.klog.adapters.model.TagWrapperModel
import java.util.UUID

interface TagsRepository {
    fun getAllTags(): TagWrapperModel
    fun getTagByTerm(term: String): TagModel?
    fun getTagById(id: UUID): TagModel?

    fun upsertTag(tag: TagModel): TagModel

    fun deleteTag(tagId: UUID)
}
