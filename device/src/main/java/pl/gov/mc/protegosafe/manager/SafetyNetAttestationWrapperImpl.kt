package pl.gov.mc.protegosafe.manager

import android.content.Context
import com.google.android.gms.safetynet.SafetyNet
import com.google.common.io.BaseEncoding
import io.reactivex.Single
import java.util.Locale
import pl.gov.mc.protegosafe.domain.extension.toSHA256
import pl.gov.mc.protegosafe.domain.manager.SafetyNetAttestationWrapper
import pl.gov.mc.protegosafe.domain.model.DiagnosisKey
import pl.gov.mc.protegosafe.extension.toSingle

class SafetyNetAttestationWrapperImpl(
    private val context: Context,
    private val safetyNetApiKey: String
) : SafetyNetAttestationWrapper {

    private val _base64: BaseEncoding = BaseEncoding.base64().omitPadding()

    override fun attestFor(keys: List<DiagnosisKey>, regions: List<String>): Single<String> {
        val cleartext: String = cleartextFor(keys, regions)
        val nonce: String = _base64.encode(cleartext.toSHA256())
        return safetyNetAttestationFor(nonce)
    }

    private fun safetyNetAttestationFor(nonce: String): Single<String> {
        return SafetyNet.getClient(context).attest(nonce.toByteArray(), safetyNetApiKey).toSingle()
            .map { attestationResponse ->
                attestationResponse.jwsResult
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
                add(_base64.encode(key.key))
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
