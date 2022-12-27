package org.nixdork.klog.adapters.data

import org.nixdork.klog.adapters.model.EntryModel
import org.nixdork.klog.adapters.model.EntryWrapperModel
import org.nixdork.klog.adapters.model.LinkModel
import org.nixdork.klog.adapters.model.PageModel
import org.nixdork.klog.adapters.model.PageWrapperModel
import org.nixdork.klog.adapters.model.PersonModel
import org.nixdork.klog.adapters.model.TagModel
import java.util.UUID

@Suppress("TooManyFunctions")
interface KlogRepository {
    fun getLastNEntries(n: Int): List<EntryWrapperModel>

    fun getAllEntries(): List<EntryWrapperModel>
    fun getEntryBySlug(slug: String): EntryModel?
    fun getEntryById(id: UUID): EntryModel?
    fun getEntriesByTag(term: String): List<EntryWrapperModel>

    fun getAllPages(): List<PageWrapperModel>
    fun getPageByPath(path: String): PageModel?
    fun getPageById(id: UUID): PageModel?

    fun getAllPeople(): List<PersonModel>
    fun getPersonByEmail(email: String): PersonModel?
    fun getPersonById(id: UUID): PersonModel?
    fun getContributorsByEntryId(entryId: UUID): List<PersonModel>
    fun getContributorsByPageId(pageId: UUID): List<PersonModel>

    fun getAllTags(): List<TagModel>
    fun getTagByTerm(term: String): TagModel?
    fun getTagById(id: UUID): TagModel?
    fun getTagsByEntryId(entryId: UUID): List<TagModel>

    fun getAllLinks(): List<LinkModel>
    fun getLinkById(id: UUID): LinkModel?
    fun getLinksByEntryId(entryId: UUID): List<LinkModel>
    fun getSourcesByEntryId(entryId: UUID): List<LinkModel>
    fun getLinksByPageId(pageId: UUID): List<LinkModel>

    fun upsertEntry(entry: EntryModel): EntryModel
    fun upsertLink(link: LinkModel): LinkModel
    fun upsertPage(page: PageModel): PageModel
    fun upsertPerson(person: PersonModel): PersonModel
    fun upsertTag(tag: TagModel): TagModel

    fun upsertEntryToLink(entryId: UUID, linkId: UUID)
    fun upsertEntryToTag(entryId: UUID, tagId: UUID)
    fun upsertEntryToContributor(entryId: UUID, contributorId: UUID)
    fun upsertEntryToSource(entryId: UUID, sourceId: UUID)
    fun upsertPageToLink(pageId: UUID, linkId: UUID)
    fun upsertPageToContributor(entryId: UUID, contributorId: UUID)

    fun deleteEntry(entryId: UUID)
    fun deletePage(pageId: UUID)
    fun deletePerson(personId: UUID)
    fun deleteLink(linkId: UUID)
    fun deleteTag(tagId: UUID)
}
