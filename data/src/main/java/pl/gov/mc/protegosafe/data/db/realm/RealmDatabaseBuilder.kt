package pl.gov.mc.protegosafe.data.db.realm

import android.os.Build
import io.realm.RealmConfiguration
import pl.gov.mc.protegosafe.data.db.SharedPreferencesDelegates
import pl.gov.mc.protegosafe.domain.extension.hexStringToByteArray
import pl.gov.mc.protegosafe.domain.extension.toHexString
import pl.gov.mc.protegosafe.domain.manager.KeystoreManager
import pl.gov.mc.protegosafe.domain.utils.CryptoUtil

class RealmDatabaseBuilder(
    private val keystoreManager: KeystoreManager,
    sharedPreferencesDelegates: SharedPreferencesDelegates
) : RealmConfiguration.Builder() {

    private var _encryptedDatabaseKey by sharedPreferencesDelegates.stringPref(
        DATABASE_KEY_SHARED_PREFERENCE, ""
    )

    override fun build(): RealmConfiguration {
        schemaVersion(DATABASE_SCHEMA_VERSION)
        migration(RealmMigrations())

        /*
         * Realm database encryption limited to Android API equal or greater 23 (M). Previous
         * Android versions doesn't support SecretKeyFactory AES encryption, please see:
         * https://developer.android.com/guide/topics/security/cryptography.
         *
         * To make possible encryption for Android version 21 and 22 the KeystoreManager has
         * to be re-implemented and workaround for missing KeyProperties.KEY_ALGORITHM_AES
         * provided.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val databaseKey = if (_encryptedDatabaseKey.isEmpty()) {
                createDatabaseKey().also { databaseKey ->
                    _encryptedDatabaseKey = encryptDatabaseKey(databaseKey)
                }
            } else {
                decryptDatabaseKey(_encryptedDatabaseKey)
            }
            encryptionKey(databaseKey)
        }

        return super.build()
    }

    private fun decryptDatabaseKey(encryptedKey: String): ByteArray {
        return CryptoUtil.decryptBytes(keystoreManager, encryptedKey.hexStringToByteArray())
    }

    private fun encryptDatabaseKey(decryptedKey: ByteArray): String {
        return CryptoUtil.encryptBytes(keystoreManager, decryptedKey).toHexString()
    }

    private fun createDatabaseKey(): ByteArray {
        return CryptoUtil.generateRandomByteArray(DATABASE_KEY_LENGTH)
    }

    companion object {
        const val DATABASE_SCHEMA_VERSION = 4L
        private const val DATABASE_KEY_SHARED_PREFERENCE = "RealmConfiguration.DATABASE_KEY_PREF"
        private const val DATABASE_KEY_LENGTH = 64
    }
}
