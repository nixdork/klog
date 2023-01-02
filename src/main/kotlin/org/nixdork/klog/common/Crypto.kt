package org.nixdork.klog.common

import java.security.SecureRandom
import java.util.HexFormat

private const val SECURE_RANDOM_ALGORITHM = "NativePRNGNonBlocking"
private const val BYTES_TO_GENERATE = 16

fun ByteArray.hexEncode(): String = HexFormat.of().formatHex(this)

fun generateSalt(): String {
    val salt = ByteArray(BYTES_TO_GENERATE)
    val srand = SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM)
    srand.nextBytes(salt)
    return salt.hexEncode()
}
