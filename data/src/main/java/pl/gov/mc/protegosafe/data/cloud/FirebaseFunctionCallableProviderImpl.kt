package pl.gov.mc.protegosafe.data.cloud

import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.HttpsCallableReference

class FirebaseFunctionCallableProviderImpl(
    private val firebaseFunctions: FirebaseFunctions
) : FirebaseFunctionCallableProvider {
    override fun getTemporaryExposureKeysUploadCallable(): HttpsCallableReference =
        getCallable(CallableName.TEMPORARY_EXPOSURE_KEYS_UPLOAD)

    override fun getAccessTokenCallable(): HttpsCallableReference =
        getCallable(CallableName.ACCESS_TOKEN)

    private fun getCallable(callableName: CallableName): HttpsCallableReference =
        firebaseFunctions.getHttpsCallable(callableName.callableName)

    private enum class CallableName(val callableName: String) {
        ACCESS_TOKEN("getAccessToken"),
        TEMPORARY_EXPOSURE_KEYS_UPLOAD("uploadDiagnosisKeys")
    }
}
