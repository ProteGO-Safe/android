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

    fun generateOrGetRealmEncryptionKey(context: Context): ByteArray {

        val appContext = context.applicationContext
        var encryptedRealmKey = loadEncryptedRealmKey(appContext)
        val keystoreContainsEncryptionKey =
            encryptionKeyStore.keystoreContainsEncryptionKey(KEY_ALIAS)
        if (encryptedRealmKey == null || !keystoreContainsEncryptionKey) {
            val realmKey = randomKey.generateKeyForRealm(RealmConfiguration.KEY_LENGTH)
            encryptionKeyStore.generateKeyInKeystore(KEY_ALIAS)
            encryptedRealmKey = encryptionKeyStore.encryptAndSaveKeyForRealm(realmKey, KEY_ALIAS)
            saveEncryptedRealmKey(context, encryptedRealmKey)
            Arrays.fill(realmKey, 0.toByte())
        }
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
        getPreference(context).edit()
            .putString(STORAGE_PREF_KEY, encodedEncryptionKey)
            .apply()
    }

    private fun loadEncryptedRealmKey(context: Context): ByteArray? {
        val pref = getPreference(context)
        val encodedEncryptionKey = pref.getString(STORAGE_PREF_KEY, null) ?: return null
        Timber.d(encodedEncryptionKey)
        return Base64.decode(encodedEncryptionKey, Base64.DEFAULT)
    }

    private fun getPreference(context: Context): SharedPreferences =
        context.getSharedPreferences(STORAGE_PREF_NAME, Context.MODE_PRIVATE)
}