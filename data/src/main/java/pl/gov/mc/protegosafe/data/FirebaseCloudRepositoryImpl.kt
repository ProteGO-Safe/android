package pl.gov.mc.protegosafe.data

import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Single
import org.json.JSONObject
import pl.gov.mc.protegosafe.data.cloud.FirebaseFunctionCallableProvider
import pl.gov.mc.protegosafe.data.extension.toCompletable
import pl.gov.mc.protegosafe.data.extension.toSingle
import pl.gov.mc.protegosafe.data.mapper.toGetAccessTokenResponseBody
import pl.gov.mc.protegosafe.data.mapper.toTemporaryExposureKeysUploadRequestBody
import pl.gov.mc.protegosafe.data.model.GetAccessTokenRequestBody
import pl.gov.mc.protegosafe.domain.model.PinItem
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeysUploadRequestItem
import pl.gov.mc.protegosafe.domain.repository.CloudRepository

class FirebaseCloudRepositoryImpl(
    private val callableProvider: FirebaseFunctionCallableProvider
) : CloudRepository {

    override fun getAccessToken(pinItem: PinItem): Single<String> {
        val requestBody = Gson().toJson(GetAccessTokenRequestBody(pinItem.pin))
        return callableProvider.getAccessTokenCallable().call(JSONObject(requestBody)).toSingle()
            .map {
                it.toGetAccessTokenResponseBody().token
            }
    }

    override fun uploadTemporaryExposureKeys(requestItem: TemporaryExposureKeysUploadRequestItem): Completable {
        val requestBody = Gson().toJson(requestItem.toTemporaryExposureKeysUploadRequestBody())
        return callableProvider.getTemporaryExposureKeysUploadCallable()
            .call(JSONObject(requestBody))
            .toCompletable()
    }
}
