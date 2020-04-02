package pl.gov.mc.protego.realm

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import io.realm.RealmConfiguration
import pl.gov.mc.protego.encryption.EncryptionKeyStore
import pl.gov.mc.protego.encryption.RandomKey
import timber.log.Timber
import java.util.*

class RealmEncryption(
    val randomKey: RandomKey,
    val encryptionKeyStore: EncryptionKeyStore
) {

    companion object {
        private val STORAGE_PREF_NAME = ".realm_key"
        private val STORAGE_PREF_KEY = "iv_and_encrypted_key"
        private val KEY_ALIAS = "realm_key"
    }

    fun ByteArray.toHexString() = joinToString("") { "%02x".format(it) }

    fun generateOrGetRealmEncryptionKey(context: Context): ByteArray {

        val appContext = context.getApplicationContext()
        var encryptedRealmKey = loadEncryptedRealmKey(appContext)
        val keystoreContainsEncryptionKey =
            encryptionKeyStore.keystoreContainsEncryptionKey(KEY_ALIAS)
        if (encryptedRealmKey == null || !keystoreContainsEncryptionKey) {
            Timber.d("no entry in keystore: keystoreContainsEncryptionKey[$keystoreContainsEncryptionKey] or not entry[$encryptedRealmKey]")
            val realmKey = randomKey.generateKeyForRealm(RealmConfiguration.KEY_LENGTH)
            encryptionKeyStore.generateKeyInKeystore(context,
                KEY_ALIAS
            )
            encryptedRealmKey = encryptionKeyStore.encryptAndSaveKeyForRealm(appContext, realmKey,
                KEY_ALIAS
            )
            saveEncryptedRealmKey(context, encryptedRealmKey)
            Arrays.fill(realmKey, 0.toByte())
        }
        Timber.d("Zaszyfrowany klucz do bazy: ${encryptedRealmKey.toHexString()}")
        return encryptionKeyStore.decryptKeyForRealm(encryptedRealmKey,
            KEY_ALIAS
        )
    }

    @SuppressLint("CommitPrefEdits")
    fun reset(context: Context) {
        getPreference(context).edit().clear().commit()
    }

    private fun saveEncryptedRealmKey(context: Context, ivAndEncryptedKey: ByteArray) {
        val encodedEncryptionKey = Base64.encodeToString(ivAndEncryptedKey, Base64.NO_WRAP)
        Timber.d("Zapisanie klucza do shared: ")
        Timber.d(encodedEncryptionKey)
        getPreference(context).edit()
            .putString(STORAGE_PREF_KEY, encodedEncryptionKey)
            .apply()
    }

    private fun loadEncryptedRealmKey(context: Context): ByteArray? {
        val pref = getPreference(context)
        val encodedEncryptionKey = pref.getString(STORAGE_PREF_KEY, null) ?: return null
        Timber.d("odczytanie klucza z shared: ")
        Timber.d(encodedEncryptionKey)
        return Base64.decode(encodedEncryptionKey, Base64.DEFAULT)
    }

    private fun getPreference(context: Context): SharedPreferences {
        return context.getSharedPreferences(STORAGE_PREF_NAME, Context.MODE_PRIVATE)
    }
}