package org.nixdork.klog.common

import java.security.SecureRandom
import java.util.HexFormat

fun ByteArray.hexEncode(): String = HexFormat.of().formatHex(this)

fun generateSalt(size: Int = CRYPTO_BYTES_TO_GENERATE): String {
    val salt = ByteArray(size)
    val srand = SecureRandom.getInstance(CRYPTO_SECURE_RANDOM_ALGORITHM)
    srand.nextBytes(salt)
    return salt.hexEncode()
}
