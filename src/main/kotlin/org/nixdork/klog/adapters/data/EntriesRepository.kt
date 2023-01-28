package org.nixdork.klog.adapters.data

import org.nixdork.klog.adapters.model.ArchiveWrapperModel
import org.nixdork.klog.adapters.model.EntryMetadataModel
import org.nixdork.klog.adapters.model.EntryModel
import org.nixdork.klog.adapters.model.EntryWrapperModel
import org.nixdork.klog.adapters.model.TagToEntriesWrapperModel
import java.util.UUID

@Suppress("TooManyFunctions")
interface EntriesRepository {
    fun getLastestNEntries(n: Int): EntryWrapperModel
    fun getAllEntries(): EntryWrapperModel
    fun getEntryBySlug(slug: String): EntryModel?
    fun getEntryById(id: UUID): EntryModel?
    fun getEntriesByTag(term: String): TagToEntriesWrapperModel
    fun getArchivedEntries(): ArchiveWrapperModel

    fun upsertEntry(authorId: UUID, entry: EntryModel): EntryModel
    fun publishEntry(entryId: UUID): EntryModel
    fun batchUpsertMetadata(entryId: UUID, metadata: List<EntryMetadataModel>)

    fun deleteEntryById(entryId: UUID)
    fun deleteEntryBySlug(slug: String)
}
