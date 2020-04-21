package pl.gov.mc.protegosafe.manager

import android.content.Context
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.safetynet.SafetyNetClient
import com.google.gson.Gson
import org.koin.core.KoinComponent
import org.koin.core.inject
import pl.gov.mc.protegosafe.domain.model.safetynet.AttestationData
import pl.gov.mc.protegosafe.domain.usecase.GetSafetyNetNonceDataUseCase
import pl.gov.mc.protegosafe.mapper.safetynet.SafetyNetMapper
import timber.log.Timber

/**
 * Checks if the device system has basic integrity and passes the CTS
 */
class SafetyNetManager(
    private val appContext: Context,
    private val safetyNetMapper: SafetyNetMapper,
    private val safetyNetClient: SafetyNetClient,
    private val googleApiAvailability: GoogleApiAvailability
) : KoinComponent {
    companion object {
        private const val SAFETYNET_API_KEY = "AIzaSyDhQUQDNpxXZ4dOMjZh4GLTtyB62Fi3U8o"
    }

    private val getSafetyNetNonceDataUseCase: GetSafetyNetNonceDataUseCase by inject()
    private val safetyNetResultValue = MutableLiveData<SafetyNetResult>()
    val safetyNetResult: LiveData<SafetyNetResult> = safetyNetResultValue


    /**
     * The nonce is returned as part of the response from the SafetyNet API.
     * Read out this value and verify it against the original request to ensure the
     * response is correct and genuine.
     * NOTE: A nonce must only be used once and a different nonce should be used for each request.
     * As a more secure option, you can obtain a nonce from your own server using a secure
     * connection. Here in this sample, we generate a String and append random bytes, which is not
     * very secure. Follow the tips on the Security Tips page for more information:
     * https://developer.android.com/training/articles/security-tips.html#Crypto
     */
    private lateinit var generatedNonce: ByteArray

    init {
        startDeviceVerification()
    }

    fun startDeviceVerification() {
        Timber.d("startDeviceVerification")
        if (isPlayServicesAvailable()) {
            generatedNonce = safetyNetMapper.generateNonce(getNonceData())
            callSafetyNetAPI(generatedNonce, SAFETYNET_API_KEY)
        } else {
            safetyNetResultValue.value = SafetyNetResult.Failure.UpdatePlayServicesError
        }
    }

    private fun callSafetyNetAPI(nonce: ByteArray, apiKey: String) {
        Timber.d("callSafetyNetAPI")
        safetyNetClient.attest(nonce, apiKey)
            .addOnSuccessListener {
                parseSafetyNetResult(it.jwsResult)
            }
            .addOnFailureListener {
                // An error occurred while communicating with the service.
                if (it is ApiException) {
                    safetyNetResultValue.value = SafetyNetResult.Failure.ConnectionError(it)
                } else {
                    safetyNetResultValue.value = SafetyNetResult.Failure.UnknownError(it)
                }
            }
    }

    private fun parseSafetyNetResult(jwsResult: String) {
        Timber.d("parseSafetyNetResult: jwsResult = [$jwsResult]")
        try {
            val jwsData = String(safetyNetMapper.extractJwsData(jwsResult))
            val attestationStatement: AttestationData = Gson().fromJson(
                jwsData,
                AttestationData::class.java
            )
            Timber.d("parseSafetyNetResult: attestationStatement = [$attestationStatement]")
            if (isNonceSame(attestationStatement.nonce) &&
                attestationStatement.ctsProfileMatch &&
                attestationStatement.basicIntegrity
            ) {
                safetyNetResultValue.value = SafetyNetResult.Success
            } else {
                safetyNetResultValue.value =
                    SafetyNetResult.Failure.SafetyError(attestationStatement.advice)
            }
        } catch (ex: IllegalArgumentException) {
            safetyNetResultValue.value = SafetyNetResult.Failure.UnknownError(ex)
        }
    }

    private fun isNonceSame(nonceFromResponse: String?): Boolean {
        return nonceFromResponse != null && Base64.encodeToString(
            generatedNonce,
            Base64.NO_WRAP
        ) == nonceFromResponse
    }

    private fun isPlayServicesAvailable() =
        googleApiAvailability.isGooglePlayServicesAvailable(appContext) == ConnectionResult.SUCCESS

    private fun getNonceData(): String {
        return getSafetyNetNonceDataUseCase.execute()
    }

    sealed class SafetyNetResult {
        object Success : SafetyNetResult()
        sealed class Failure : SafetyNetResult() {
            object UpdatePlayServicesError : Failure()
            class SafetyError(val recommendationMessage: String?) : Failure()
            class ConnectionError(val exception: ApiException?) : Failure()
            class UnknownError(val exception: Exception?) : Failure()
        }
    }
}