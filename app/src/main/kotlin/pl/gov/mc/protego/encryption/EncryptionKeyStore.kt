package pl.gov.mc.protego.encryption

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec


class EncryptionKeyStore {

    private val mKeyStore = prepareKeyStore()

    fun keystoreContainsEncryptionKey(keyAlias: String) = mKeyStore.containsAlias(keyAlias)

    fun generateKeyInKeystore(keyAlias: String) {
        val keyGenerator: KeyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE_PROVIDER_NAME
        )

        val keySpec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .setUserAuthenticationRequired(false)
            .build()

        keyGenerator.init(keySpec)

        keyGenerator.generateKey()

    }

    private fun prepareKeyStore() = KeyStore.getInstance(KEYSTORE_PROVIDER_NAME)
        .apply {
            load(null)
        }

    private fun prepareCipher(): Cipher =
        Cipher.getInstance(CIPHER)

    fun encryptAndSaveKeyForRealm(
        keyForRealm: ByteArray,
        keyAlias: String
    ): ByteArray {
        val ks = prepareKeyStore()
        val cipher = prepareCipher()

        val iv: ByteArray?
        val encryptedKeyForRealm: ByteArray

        val key: Key = ks.getKey(keyAlias, null)

        cipher.init(Cipher.ENCRYPT_MODE, key)

        encryptedKeyForRealm = cipher.doFinal(keyForRealm)
        iv = cipher.iv

        val ivLength = iv?.size ?: 0
        val ivAndEncryptedKey = ByteArray(Integer.SIZE + ivLength + encryptedKeyForRealm.size)

        val buffer = ByteBuffer.wrap(ivAndEncryptedKey)
        buffer.order(ORDER_FOR_ENCRYPTED_DATA)
        buffer.putInt(ivLength)
        if (ivLength > 0) {
            buffer.put(iv)
        }
        buffer.put(encryptedKeyForRealm)
        return ivAndEncryptedKey
    }

    fun decryptKeyForRealm(ivAndEncryptedKey: ByteArray?, keyAlias: String): ByteArray {
        val cipher = prepareCipher()
        val ks = prepareKeyStore()

        val buffer = ByteBuffer.wrap(ivAndEncryptedKey)
        buffer.order(ORDER_FOR_ENCRYPTED_DATA)

        val ivLength = buffer.int
        val iv = if (ivLength > 0) ByteArray(ivLength) else null
        val encryptedKey = ByteArray(ivAndEncryptedKey!!.size - Integer.SIZE - ivLength)

        if (iv != null) {
            buffer.get(iv)
        }
        buffer.get(encryptedKey)

        val key = ks.getKey(keyAlias, null)

        if (iv != null) {
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key)
        }

        return cipher.doFinal(encryptedKey)
    }

    companion object {

        private const val KEYSTORE_PROVIDER_NAME = "AndroidKeyStore"

        private const val CIPHER = (KeyProperties.KEY_ALGORITHM_AES + "/"
                + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7)

        private val ORDER_FOR_ENCRYPTED_DATA = ByteOrder.BIG_ENDIAN
    }

}