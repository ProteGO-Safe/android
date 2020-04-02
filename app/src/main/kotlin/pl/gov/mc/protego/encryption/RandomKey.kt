package pl.gov.mc.protego.encryption

import java.security.SecureRandom

class RandomKey(val secureRandom: SecureRandom) {

    fun generateKeyForRealm(keyLength: Int): ByteArray =
        ByteArray(keyLength)
        .also {
            secureRandom.nextBytes(it)
        }
}