package org.nixdork.klog.adapters.model

data class TagWrapperModel(
    val tag: TagModel,
    val entries: List<EntryModel>
)
