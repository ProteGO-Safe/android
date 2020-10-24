package pl.gov.mc.protegosafe.manager

import android.content.Context
import com.google.android.gms.safetynet.SafetyNet
import com.google.common.io.BaseEncoding
import com.google.gson.Gson
import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.manager.SafetyNetAttestationWrapper
import pl.gov.mc.protegosafe.domain.model.DiagnosisKey
import pl.gov.mc.protegosafe.domain.model.SafetyNetResult
import pl.gov.mc.protegosafe.extension.toSingle
import pl.gov.mc.protegosafe.model.AttestationData
import timber.log.Timber
import java.util.Locale

class SafetyNetAttestationWrapperImpl(
    private val context: Context,
    private val safetyNetApiKey: String
) : SafetyNetAttestationWrapper {

    private val _base64: BaseEncoding = BaseEncoding.base64()

    override fun getTokenFor(byteArray: ByteArray): Single<String> {
        val nonce = _base64.encode(byteArray)
        return safetyNetAttestationFor(nonce)
    }

    override fun attestFor(byteArray: ByteArray): Single<SafetyNetResult> {
        val nonce = _base64.encode(byteArray)
        return safetyNetAttestationFor(nonce)
            .map { parseSafetyNetResult(nonce, it) }
    }

    private fun safetyNetAttestationFor(nonce: String): Single<String> {
        return SafetyNet.getClient(context).attest(nonce.toByteArray(), safetyNetApiKey).toSingle()
            .map { attestationResponse ->
                attestationResponse.jwsResult
            }
    }

    private fun parseSafetyNetResult(nonce: String, jwsResult: String): SafetyNetResult {
        try {
            val jwsData = String(extractJwsData(jwsResult))
            val attestationStatement: AttestationData = Gson().fromJson(
                jwsData,
                AttestationData::class.java
            )
            return if (isNonceSame(nonce, attestationStatement.nonce) &&
                attestationStatement.ctsProfileMatch &&
                attestationStatement.basicIntegrity
            ) {
                SafetyNetResult.Success
            } else {
                SafetyNetResult.Failure.SafetyError
            }
        } catch (ex: IllegalArgumentException) {
            Timber.e("Exception: $ex")
            return SafetyNetResult.Failure.UnknownError(ex)
        }
    }

    /**
     * Extracts the data part from a JWS signature.
     */
    @Throws(java.lang.IllegalArgumentException::class)
    private fun extractJwsData(jws: String): ByteArray {
        // The format of a JWS is:
        // <Base64url encoded header>.<Base64url encoded JSON data>.<Base64url encoded signature>
        // Split the JWS into the 3 parts and return the JSON data part.
        val parts = jws.split("[.]".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (parts.size != 3) {
            throw java.lang.IllegalArgumentException(
                "Failure: Illegal JWS signature format. The JWS consists of " + parts.size + " parts instead of 3."
            )
        }
        return _base64.decode(parts[1])
    }

    private fun isNonceSame(nonce: String, nonceFromResponse: String?): Boolean {
        return (
            nonceFromResponse != null &&
                _base64.encode(nonce.toByteArray()) == nonceFromResponse
            ).also { result ->
            Timber.d("isNonceSame result: $result")
        }
    }

    private fun cleartextFor(keys: List<DiagnosisKey>, regions: List<String>): String {
        return StringBuilder().apply {
            append(context.packageName)
            appendKeys(this, keys)
            appendRegions(this, regions)
        }.toString()
    }

    private fun appendKeys(stringBuilder: StringBuilder, diagnosisKeys: List<DiagnosisKey>) {
        val diagnosisKeysBase64 = mutableListOf<String>().apply {
            for (key in diagnosisKeys) {
                add(_base64.omitPadding().encode(key.key))
            }
        }.also {
            it.sort()
        }

        for (key in diagnosisKeysBase64) {
            stringBuilder.append(key)
        }
    }

    private fun appendRegions(stringBuilder: StringBuilder, regions: List<String>) {
        // Careful: Collections.sort mutates the list in place, so make a defensive copy.
        val regionsList = mutableListOf<String>().apply {
            addAll(regions)
        }.also {
            it.sort()
        }

        for (region in regionsList) {
            // In case a caller sent us lowercase region codes (even though that wouldn't be
            // ISO_Alpha-2).
            stringBuilder.append(region.toUpperCase(Locale.ENGLISH))
        }
    }
}
