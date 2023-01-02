package org.nixdork.klog.common.exceptions

import org.nixdork.klog.common.buildLogMessage
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

open class ResourceNotFoundException(reason: String? = null) :
    ResponseStatusException(HttpStatus.NOT_FOUND, reason ?: "Not Found")

class TagNotFoundException(term: String) :
    ResourceNotFoundException(buildLogMessage("Tag not found", "term" to term))
