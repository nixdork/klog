package org.nixdork.klog.common

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.HexFormat

fun ByteArray.hexEncode(): String = HexFormat.of().formatHex(this)

fun generateSalt(size: Int = CRYPTO_BYTES_TO_GENERATE): String =
    ByteArray(size).also { salt ->
        SecureRandom.getInstance(CRYPTO_SALT_RANDOM_ALGORITHM).apply {
            nextBytes(salt)
        }
    }.hexEncode()

fun generateHash(message: String, salt: String): String =
    MessageDigest.getInstance(CRYPTO_HASH_RANDOM_ALGORITHM)
        .digest((message + salt).toByteArray())
        .hexEncode()
