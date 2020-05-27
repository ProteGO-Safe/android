package pl.gov.mc.protegosafe.domain.extension

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

const val HASH_ALGORITHM = "SHA-256"

fun String.toSHA256(): ByteArray {
    val textBytes = toByteArray(StandardCharsets.UTF_8)
    val sha256Digest = MessageDigest.getInstance(HASH_ALGORITHM).apply {
        update(textBytes)
    }
    return sha256Digest.digest()
}

fun String.hexStringToByteArray(): ByteArray {
    val data = ByteArray(length / 2)
    var i = 0
    while (i < length) {
        data[i / 2] =
            ((Character.digit(this[i], 16) shl 4) + Character.digit(this[i + 1], 16)).toByte()
        i += 2
    }
    return data
}
