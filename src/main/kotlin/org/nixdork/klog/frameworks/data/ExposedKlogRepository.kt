package org.nixdork.klog.frameworks.data

import org.nixdork.klog.adapters.data.KlogRepository
import org.nixdork.klog.adapters.model.EntryModel
import org.nixdork.klog.adapters.model.EntryWrapperModel
import org.nixdork.klog.adapters.model.LinkModel
import org.nixdork.klog.adapters.model.PageModel
import org.nixdork.klog.adapters.model.PageWrapperModel
import org.nixdork.klog.adapters.model.PersonModel
import org.nixdork.klog.adapters.model.TagModel
import org.springframework.stereotype.Component
import java.util.UUID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.nixdork.klog.frameworks.data.dao.EntryToLink
import org.nixdork.klog.frameworks.data.dao.EntryToTag
import org.nixdork.klog.frameworks.data.dao.Link
import org.nixdork.klog.frameworks.data.dao.Links
import org.nixdork.klog.frameworks.data.dao.Tag
import org.nixdork.klog.frameworks.data.dao.Tags

@Suppress("TooManyFunctions")
@Component
class ExposedKlogRepository : KlogRepository {
    override fun getLastNEntries(n: Int): List<EntryWrapperModel> =
        transaction {

        }

    override fun getAllEntries(): List<EntryWrapperModel> {
        TODO("Not yet implemented")
    }

    override fun getEntryBySlug(slug: String): EntryModel? {
        TODO("Not yet implemented")
    }

    override fun getEntryById(id: UUID): EntryModel? {
        TODO("Not yet implemented")
    }

    override fun getEntriesByTag(term: String): List<EntryWrapperModel> {
        TODO("Not yet implemented")
    }

    override fun getAllPages(): List<PageWrapperModel> {
        TODO("Not yet implemented")
    }

    override fun getPageByPath(path: String): PageModel? {
        TODO("Not yet implemented")
    }

    override fun getPageById(id: UUID): PageModel? {
        TODO("Not yet implemented")
    }

    override fun getAllPeople(): List<PersonModel> {
        TODO("Not yet implemented")
    }

    override fun getPersonByEmail(email: String): PersonModel? {
        TODO("Not yet implemented")
    }

    override fun getPersonById(id: UUID): PersonModel? {
        TODO("Not yet implemented")
    }

    override fun getContributorsByEntryId(entryId: UUID): List<PersonModel> {
        TODO("Not yet implemented")
    }

    override fun getContributorsByPageId(pageId: UUID): List<PersonModel> {
        TODO("Not yet implemented")
    }

    override fun getAllTags(): List<TagModel> = transaction { Tag.all().map { it.toModel() } }

    override fun getTagByTerm(term: String): TagModel? =
        transaction { Tag.find(Tags.term eq term).singleOrNull()?.toModel() }


    override fun getTagById(id: UUID): TagModel? = transaction { Tag.findById(id)?.toModel() }

    override fun getTagsByEntryId(entryId: UUID): List<TagModel> {
        val tagIds = EntryToTag.select { EntryToTag.entryId eq entryId }.map { it[EntryToTag.tagId].value }
        return Tags.select { Tags.id inList tagIds }.map { Tag.wrapRow(it).toModel() }
    }

    override fun getAllLinks(): List<LinkModel> = transaction { Link.all().map { it.toModel() } }

    override fun getLinkById(id: UUID): LinkModel? = transaction { Link.findById(id)?.toModel() }

    override fun getLinksByEntryId(entryId: UUID): List<LinkModel> =
        transaction {
            val linkIds = EntryToLink.select { EntryToLink.entryId eq entryId }.map { it[EntryToLink.linkId].value }
            Links.select { Links.id inList linkIds }.map { Link.wrapRow(it).toModel() }
        }


    override fun getSourcesByEntryId(entryId: UUID): List<LinkModel> =
        transaction {
            val sourceIds = EntryToLink.select { EntryToLink.entryId eq entryId }.map { it[EntryToLink.linkId].value }
            Links.select { Links.id inList sourceIds }.map { Link.wrapRow(it).toModel() }
        }

    override fun getLinksByPageId(pageId: UUID): List<LinkModel> {
        TODO("Not yet implemented")
    }

    override fun upsertEntry(entry: EntryModel): EntryModel {
        TODO("Not yet implemented")
    }

    override fun upsertLink(link: LinkModel): LinkModel {
        TODO("Not yet implemented")
    }

    override fun upsertPage(page: PageModel): PageModel {
        TODO("Not yet implemented")
    }

    override fun upsertPerson(person: PersonModel): PersonModel {
        TODO("Not yet implemented")
    }

    override fun upsertTag(tag: TagModel): TagModel {
        TODO("Not yet implemented")
    }

    override fun upsertEntryToLink(entryId: UUID, linkId: UUID) {
        TODO("Not yet implemented")
    }

    override fun upsertEntryToTag(entryId: UUID, tagId: UUID) {
        TODO("Not yet implemented")
    }

    override fun upsertEntryToContributor(entryId: UUID, contributorId: UUID) {
        TODO("Not yet implemented")
    }

    override fun upsertEntryToSource(entryId: UUID, sourceId: UUID) {
        TODO("Not yet implemented")
    }

    override fun upsertPageToLink(pageId: UUID, linkId: UUID) {
        TODO("Not yet implemented")
    }

    override fun upsertPageToContributor(entryId: UUID, contributorId: UUID) {
        TODO("Not yet implemented")
    }

    override fun deleteEntry(entryId: UUID) {
        TODO("Not yet implemented")
    }

    override fun deletePage(pageId: UUID) {
        TODO("Not yet implemented")
    }

    override fun deletePerson(personId: UUID) {
        TODO("Not yet implemented")
    }

    override fun deleteLink(linkId: UUID) {
        TODO("Not yet implemented")
    }

    override fun deleteTag(tagId: UUID) {
        TODO("Not yet implemented")
    }
}
