package org.nixdork.klog.common

import java.text.Normalizer
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.HexFormat
import java.util.UUID

fun String.toUUID(): UUID = UUID.fromString(this)

fun ByteArray.hexEncode(): String = HexFormat.of().formatHex(this)

fun String.slugify(): String = Normalizer.normalize(this, Normalizer.Form.NFD)
    .replace("[^\\w\\s-]".toRegex(), "")
    .replace('-', ' ')
    .trim()
    .replace("\\s+".toRegex(), "-")
    .lowercase()

// A `kotlinx.datetime.Instant`
fun Instant.toOffsetDateTime(): OffsetDateTime = OffsetDateTime.ofInstant(this, ZoneOffset.UTC)

fun OffsetDateTime.normalize(): OffsetDateTime = truncatedTo(ChronoUnit.MILLIS)
