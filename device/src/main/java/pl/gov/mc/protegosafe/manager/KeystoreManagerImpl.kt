package pl.gov.mc.protegosafe.manager

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import pl.gov.mc.protegosafe.domain.manager.KeystoreManager

class KeystoreManagerImpl : KeystoreManager {
    private val keyStore: KeyStore = KeyStore.getInstance(KEYSTORE_TYPE).also {
        it.load(null)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun getSecretKey(alias: String): SecretKey {
        return if (keyStore.containsAlias(alias)) {
            (keyStore.getEntry(alias, null) as KeyStore.SecretKeyEntry).secretKey
        } else {
            generateSecretKey(alias)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun generateSecretKey(alias: String): SecretKey {
        val keyGenerator = KeyGenerator
            .getInstance(KeyProperties.KEY_ALGORITHM_AES, KEYSTORE_TYPE)

        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(false)
                .build()
        )

        return keyGenerator.generateKey()
    }

    companion object {
        private const val KEYSTORE_TYPE = "AndroidKeyStore"
    }
}
