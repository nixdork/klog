package org.nixdork.klog.adapters.data

import org.nixdork.klog.adapters.model.EntryModel
import org.nixdork.klog.adapters.model.EntryWrapperModel
import org.nixdork.klog.adapters.model.PersonModel
import org.nixdork.klog.adapters.model.TagModel
import java.util.UUID
import org.nixdork.klog.adapters.model.ArchiveWrapperModel
import org.nixdork.klog.adapters.model.EntryMetadataModel
import org.nixdork.klog.adapters.model.PersonPasswordModel
import org.nixdork.klog.adapters.model.TagToEntriesWrapperModel
import org.nixdork.klog.adapters.model.TagWrapperModel

@Suppress("TooManyFunctions")
interface KlogRepository {
    fun getLastestNEntries(n: Int): EntryWrapperModel

    fun getAllEntries(): EntryWrapperModel
    fun getEntryBySlug(slug: String): EntryModel?
    fun getEntryById(id: UUID): EntryModel?
    fun getEntriesByTag(term: String): TagToEntriesWrapperModel
    fun getArchivedEntries(): ArchiveWrapperModel

    fun getAllPeople(): List<PersonModel>
    fun getPersonByEmail(email: String): PersonModel?
    fun getPersonById(id: UUID): PersonModel?
    fun getPasswordByEmail(email: String): PersonPasswordModel?
    fun verifyPassword(password: PersonPasswordModel): Boolean

    fun getAllTags(): TagWrapperModel
    fun getTagByTerm(term: String): TagModel?
    fun getTagById(id: UUID): TagModel?

    fun updateLastLogin(personId: UUID): PersonModel?
    fun upsertEntry(authorId: UUID, entry: EntryModel): EntryModel
    fun publishEntry(entryId: UUID): EntryModel
    fun batchUpsertMetadata(entryId: UUID, metadata: List<EntryMetadataModel>)
    fun upsertPerson(person: PersonModel): PersonModel
    fun upsertPassword(password: PersonPasswordModel): PersonModel
    fun upsertTag(tag: TagModel): TagModel

    fun deleteEntry(entryId: UUID)
    fun deletePerson(personId: UUID)
    fun deleteTag(tagId: UUID)
}
