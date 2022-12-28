package org.nixdork.klog.adapters.model

data class EntryWrapperModel(
    val entry: EntryModel,
    val links: List<LinkModel>,
    val tags: List<TagModel>,
    val contributors: List<PersonModel>,
    val sources: List<LinkModel>
)
