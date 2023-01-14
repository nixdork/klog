package org.nixdork.klog.adapters.model

import java.time.LocalDate

data class ArchiveModel(
    val date: LocalDate,
    val year: Int,
    val month: Int,
    val day: Int,
    val monthYear: String, // "2022-01" sortable
    val entries: List<EntryModel>
)
