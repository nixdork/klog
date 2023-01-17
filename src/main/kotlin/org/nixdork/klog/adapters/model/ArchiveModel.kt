package org.nixdork.klog.adapters.model

import java.time.LocalDate

data class ArchiveModel(
    val date: LocalDate,
    val year: Int? = date.year,
    val month: Int? = date.month.value,
    val day: Int? = date.dayOfMonth,
    val monthYear: String? = "${date.year}-${date.month.value}", // "2022-01" sortable
    val entries: List<EntryModel>
)
