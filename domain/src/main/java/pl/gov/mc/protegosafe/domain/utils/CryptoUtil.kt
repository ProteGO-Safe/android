package pl.gov.mc.protegosafe.domain.utils

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import pl.gov.mc.protegosafe.domain.manager.KeystoreManager

object CryptoUtil {
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val LENGTH = 128
    private const val ALIAS = "InternalKey"
    private const val INPUT_VECTOR_LENGTH_IN_BYTES = 12

    private fun generateInternalIv(): ByteArray {
        val result = ByteArray(INPUT_VECTOR_LENGTH_IN_BYTES)
        for (i in 0 until INPUT_VECTOR_LENGTH_IN_BYTES) result[i] = (122 - i).toByte()
        return result
    }

    fun encryptBytes(
        keystoreManager: KeystoreManager,
        bytesToEncrypt: ByteArray
    ): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val initVector = generateInternalIv()
        cipher.init(
            Cipher.ENCRYPT_MODE,
            keystoreManager.getSecretKey(ALIAS),
            GCMParameterSpec(LENGTH, initVector)
        )
        return cipher.doFinal(bytesToEncrypt)
    }

    fun decryptBytes(
        keystoreManager: KeystoreManager,
        encryptedBytes: ByteArray
    ): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val initVector = generateInternalIv()
        cipher.init(
            Cipher.DECRYPT_MODE,
            keystoreManager.getSecretKey(ALIAS),
            GCMParameterSpec(LENGTH, initVector)
        )
        return cipher.doFinal(encryptedBytes)
    }

    fun generateRandomByteArray(length: Int): ByteArray {
        val secureRandom = SecureRandom()
        val randomBytes = ByteArray(length)
        secureRandom.nextBytes(randomBytes)
        return randomBytes
    }
}
