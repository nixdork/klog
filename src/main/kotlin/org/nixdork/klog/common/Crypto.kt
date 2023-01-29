package org.nixdork.klog.common

import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

fun generateSalt(size: Int = CRYPTO_BYTES_TO_GENERATE): String =
    ByteArray(size).also { salt ->
        SecureRandom.getInstance(CRYPTO_SALT_ALGORITHM).apply {
            nextBytes(salt)
        }
    }.hexEncode()

fun generateHash(message: String, salt: String): String =
    MessageDigest.getInstance(CRYPTO_HASH_ALGORITHM)
        .digest((message + salt).toByteArray())
        .hexEncode()

fun generateHash2(password: String, salt: String): String =
    SecretKeyFactory.getInstance(CRYPTO_HASH_ALGORITHM2).let { factory ->
        "$salt$CRYPTO_HASH_SECRET".toByteArray().let { combinedSalt ->
            PBEKeySpec(
                password.toCharArray(),
                combinedSalt,
                CRYPTO_HASH_ITERATIONS,
                CRYPTO_HASH_KEY_LENGTH,
            ).let { spec ->
                factory.generateSecret(spec).encoded
            }
        }
    }.hexEncode()
