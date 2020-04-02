package pl.gov.mc.protego.encryption

import io.realm.RealmConfiguration
import java.security.SecureRandom

class RandomKey(val secureRandom: SecureRandom) {

//    private val mSecureRandom = SecureRandom()

//    RealmConfiguration.KEY_LENGTH)
    fun generateKeyForRealm(keyLength: Int): ByteArray {
        val keyForRealm = ByteArray(keyLength)
        secureRandom.nextBytes(keyForRealm)
        return keyForRealm
    }
}