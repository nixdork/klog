package org.nixdork.klog.adapters.model

import java.time.LocalDate

data class ArchiveModel(
    val date: LocalDate,
    val year: Int? = date.year,
    val month: Int? = date.month.value,
    val monthYear: String? = "${date.year}-${date.month.value.toString().padStart(2, '0')}", // "2022-01" sortable
    val entries: List<EntryModel>
)
