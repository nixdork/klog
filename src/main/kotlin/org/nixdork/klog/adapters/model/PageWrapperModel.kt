package org.nixdork.klog.adapters.model

data class PageWrapperModel(
    val page: PageModel,
    val links: List<LinkModel>,
    val contributors: List<PersonModel>
)
