package pl.gov.mc.protego.encryption

import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyProperties
import android.security.keystore.KeyGenParameterSpec
import android.content.Context
import android.os.Build
import java.io.IOException
import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.security.*
import java.security.cert.CertificateException
import java.util.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.security.auth.x500.X500Principal
import timber.log.Timber


class EncryptionKeyStore {

    private val mKeyStore = prepareKeyStore()

    fun keystoreContainsEncryptionKey(keyAlias: String): Boolean {
        try {
            return mKeyStore.containsAlias(keyAlias).also {
                Timber.d("Key for alias[$keyAlias] - $it")
            }
        } catch (e: KeyStoreException) {
            throw RuntimeException(e)
        }

    }

    fun generateKeyInKeystore(context: Context, keyAlias: String) {
        try {
            if (isApi23) {
                val keyGenerator: KeyGenerator
                keyGenerator = KeyGenerator.getInstance(
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

                try {
                    keyGenerator.init(keySpec)
                } catch (e: InvalidAlgorithmParameterException) {
                    throw RuntimeException(e)
                }

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
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: NoSuchProviderException) {
            throw RuntimeException(e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw RuntimeException(e)
        }

    }

    private fun prepareKeyStore(): KeyStore {
        try {
            val ks = KeyStore.getInstance(KEYSTORE_PROVIDER_NAME)
            ks.load(null)
            return ks
        } catch (e: KeyStoreException) {
            throw RuntimeException(e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: CertificateException) {
            throw RuntimeException(e)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }

    private fun prepareCipher(): Cipher {
        val cipher: Cipher
        try {
            cipher = Cipher.getInstance(CIPHER)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: NoSuchPaddingException) {
            throw RuntimeException(e)
        }

        return cipher
    }

    fun encryptAndSaveKeyForRealm(context: Context, keyForRealm: ByteArray, keyAlias: String): ByteArray {
        val ks = prepareKeyStore()
        val cipher = prepareCipher()

        val iv: ByteArray?
        val encryptedKeyForRealm: ByteArray
        try {
            val key: Key
            if (isApi23) {
                key = ks.getKey(keyAlias, null)
            } else {
                val privateKeyEntry: KeyStore.PrivateKeyEntry
                try {
                    privateKeyEntry = ks.getEntry(keyAlias, null) as KeyStore.PrivateKeyEntry
                    key = privateKeyEntry.getCertificate().getPublicKey()
                } catch (e: UnrecoverableEntryException) {
                    throw RuntimeException("key for encryption is invalid", e)
                }

            }
            cipher.init(Cipher.ENCRYPT_MODE, key)

            encryptedKeyForRealm = cipher.doFinal(keyForRealm)
            iv = cipher.getIV()
        } catch (e: InvalidKeyException) {
            throw RuntimeException("key for encryption is invalid", e)
        } catch (e: UnrecoverableKeyException) {
            throw RuntimeException("key for encryption is invalid", e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("key for encryption is invalid", e)
        } catch (e: KeyStoreException) {
            throw RuntimeException("key for encryption is invalid", e)
        } catch (e: BadPaddingException) {
            throw RuntimeException("key for encryption is invalid", e)
        } catch (e: IllegalBlockSizeException) {
            throw RuntimeException("key for encryption is invalid", e)
        }

        val ivLength = if (iv != null) iv!!.size else 0
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

        val ivLength = buffer.getInt()
        val iv = if (ivLength > 0) ByteArray(ivLength) else null
        val encryptedKey = ByteArray(ivAndEncryptedKey!!.size - Integer.SIZE - ivLength)

        if (iv != null) {
            buffer.get(iv)
        }
        buffer.get(encryptedKey)

        try {
            val key = ks.getKey(keyAlias, null)

            if (iv != null) {
                val ivSpec = IvParameterSpec(iv)
                cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)
            } else {
                cipher.init(Cipher.DECRYPT_MODE, key)
            }

            return cipher.doFinal(encryptedKey)
        } catch (e: InvalidKeyException) {
            throw RuntimeException("key is invalid.")
        } catch (e: UnrecoverableKeyException) {
            throw RuntimeException(e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: BadPaddingException) {
            throw RuntimeException(e)
        } catch (e: KeyStoreException) {
            throw RuntimeException(e)
        } catch (e: IllegalBlockSizeException) {
            throw RuntimeException(e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw RuntimeException(e)
        }

    }

    companion object {

        private val KEYSTORE_PROVIDER_NAME = "AndroidKeyStore"
        private val KEY_COMMON_NAME = "RealmEncryption"

        private val CIPHER_API_18 = "RSA/ECB/PKCS1Padding"
        private val CIPHER_API_23 = (KeyProperties.KEY_ALGORITHM_AES + "/"
                + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7)
        private val CIPHER = if (isApi23) CIPHER_API_23 else CIPHER_API_18
        private val TYPE_RSA = "RSA"

        private val ORDER_FOR_ENCRYPTED_DATA = ByteOrder.BIG_ENDIAN

        private val isApi23: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

}