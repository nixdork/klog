package org.nixdork.klog.common

import mu.KLogger
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl

/**
 * Builds a nice log message
 *
 * @param message The log message
 * @param list A list of tags to append to the log message
 */
fun buildLogMessage(message: String, list: List<Pair<String, Any?>> = emptyList()) =
    buildString {
        if (message.isNotBlank()) {
            append(message)
        }
        if (list.isNotEmpty()) {
            append(" ")
            append(
                list.joinToString(separator = ", ", prefix = "[", postfix = "]") { (name, value) -> "$name: $value" }
            )
        }
    }

fun buildLogMessage(message: String, vararg values: Pair<String, Any?>) = buildLogMessage(message, values.toList())

fun CoRouterFunctionDsl.logExchange(log: KLogger) {
    before { request ->
        log.info { "<== ${request.method()} ${request.path()}" }
        request
    }
    after { request, response ->
        log.info { "==> ${response.statusCode()} | ${request.method()} ${request.path()}" }
        response
    }
}
