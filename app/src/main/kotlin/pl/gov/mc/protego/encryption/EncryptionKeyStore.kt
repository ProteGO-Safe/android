package pl.gov.mc.protego.encryption

import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyGenParameterSpec
import android.content.Context
import android.os.Build
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.*
import java.util.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.security.auth.x500.X500Principal


class EncryptionKeyStore {

    private val mKeyStore = prepareKeyStore()

    fun keystoreContainsEncryptionKey(keyAlias: String) = mKeyStore.containsAlias(keyAlias)

    fun generateKeyInKeystore(context: Context, keyAlias: String) {
        if (isApi23) {
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
        } else {
            val start = GregorianCalendar()
            val end = GregorianCalendar()
            end.add(Calendar.YEAR, 25)

            val spec = KeyPairGeneratorSpec.Builder(context)
                .setAlias(keyAlias)
                .setSubject(X500Principal("CN=$KEY_COMMON_NAME"))
                .setSerialNumber(BigInteger.valueOf(1337))
                .setStartDate(start.getTime())
                .setEndDate(end.getTime())
                .build()

            val kpGenerator = KeyPairGenerator.getInstance(TYPE_RSA, KEYSTORE_PROVIDER_NAME)
            kpGenerator.initialize(spec)
            kpGenerator.generateKeyPair()
        }
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

        val key: Key
        if (isApi23) {
            key = ks.getKey(keyAlias, null)
        } else {
            val privateKeyEntry: KeyStore.PrivateKeyEntry
            try {
                privateKeyEntry = ks.getEntry(keyAlias, null) as KeyStore.PrivateKeyEntry
                key = privateKeyEntry.certificate.publicKey
            } catch (e: UnrecoverableEntryException) {
                throw RuntimeException("key for encryption is invalid", e)
            }

        }
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
        private const val KEY_COMMON_NAME = "RealmEncryption"

        private const val CIPHER_API_18 = "RSA/ECB/PKCS1Padding"
        private const val CIPHER_API_23 = (KeyProperties.KEY_ALGORITHM_AES + "/"
                + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7)
        private val CIPHER = if (isApi23) CIPHER_API_23 else CIPHER_API_18
        private const val TYPE_RSA = "RSA"

        private val ORDER_FOR_ENCRYPTED_DATA = ByteOrder.BIG_ENDIAN

        private val isApi23: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

}