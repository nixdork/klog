package org.nixdork.klog.adapters.model

data class ArchiveModel(
    val monthYear: String,
    val entries: List<EntryModel>
)
