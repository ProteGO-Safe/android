package pl.gov.mc.protegosafe.data.cloud

import com.google.firebase.functions.HttpsCallableReference

interface FirebaseFunctionCallableProvider {
    fun getAccessTokenCallable(): HttpsCallableReference
    fun getTemporaryExposureKeysUploadCallable(): HttpsCallableReference
}
