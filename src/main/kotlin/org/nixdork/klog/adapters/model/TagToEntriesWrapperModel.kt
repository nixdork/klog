package org.nixdork.klog.adapters.model

data class TagToEntriesWrapperModel(
    val tag: TagModel,
    val entries: List<EntryModel>,
)
