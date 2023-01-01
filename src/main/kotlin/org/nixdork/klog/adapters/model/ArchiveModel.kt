package org.nixdork.klog.adapters.model

import java.time.LocalDate

data class ArchiveModel(
    val date: LocalDate,
    val monthYear: String,
    val entries: List<EntryModel>
)
