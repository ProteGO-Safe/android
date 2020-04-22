package pl.gov.mc.protegosafe.mapper.safetynet

import android.util.Base64
import java.io.ByteArrayOutputStream
import java.lang.IllegalArgumentException
import java.security.SecureRandom

/**
 * Helper class for [SafetyNetMapper]
 */
class SafetyNetMapper {

    /**
     * Generates a 16-byte nonce with additional data.
     * The nonce should also include additional information, such as a user id or any other details
     * you wish to bind to this attestation. Here you can provide a String that is included in the
     * nonce after 24 random bytes. During verification, extract this data again and check it
     * against the request that was made with this nonce.
     */
    fun generateNonce(data: String): ByteArray {
        val byteStream = ByteArrayOutputStream()
        val bytes = ByteArray(24)
        SecureRandom().nextBytes(bytes)
        byteStream.use {
            it.write(bytes)
            it.write(data.toByteArray())
            return it.toByteArray()
        }
    }

    /**
     * Extracts the data part from a JWS signature.
     */
    @Throws(IllegalArgumentException::class)
    fun extractJwsData(jws: String): ByteArray {
        // The format of a JWS is:
        // <Base64url encoded header>.<Base64url encoded JSON data>.<Base64url encoded signature>
        // Split the JWS into the 3 parts and return the JSON data part.
        val parts = jws.split("[.]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (parts.size != 3) {
            throw IllegalArgumentException(
                "Failure: Illegal JWS signature format. The JWS consists of " + parts.size + " parts instead of 3."
            )
        }
        return Base64.decode(parts[1], Base64.DEFAULT)
    }
}