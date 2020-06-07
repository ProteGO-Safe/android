package pl.gov.mc.protegosafe.data.cloud

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.annotations.CheckReturnValue
import pl.gov.mc.protegosafe.data.BuildConfig
import pl.gov.mc.protegosafe.data.model.GetAccessTokenResponseData
import pl.gov.mc.protegosafe.data.model.RequestBody
import pl.gov.mc.protegosafe.data.model.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST

interface UploadTemporaryExposureKeysService {
    @POST(BuildConfig.GET_ACCESS_TOKEN_ENDPOINT)
    @CheckReturnValue
    fun getAccessToken(@Body requestBody: RequestBody): Single<ResponseBody<GetAccessTokenResponseData>>

    @POST(BuildConfig.UPLOAD_BUCKET_ENDPOINT)
    fun uploadDiagnosisKeys(@Body requestBody: RequestBody): Completable
}
