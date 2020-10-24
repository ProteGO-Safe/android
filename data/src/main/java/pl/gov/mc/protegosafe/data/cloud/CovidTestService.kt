package pl.gov.mc.protegosafe.data.cloud

import io.reactivex.Single
import pl.gov.mc.protegosafe.data.BuildConfig
import pl.gov.mc.protegosafe.data.model.GetAccessTokenResponseData
import pl.gov.mc.protegosafe.data.model.covidtest.TestSubscriptionRequestData
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CovidTestService {

    @POST("${BuildConfig.COVID_TEST_URL}createSubscription")
    fun createSubscription(
        @Header(SAFETY_TOKEN_HEADER) safetynetToken: String,
        @Body testSubscriptionRequestData: TestSubscriptionRequestData,
        @Header(USER_AGENT_HEADER) userAgent: String = USER_AGENT_ANDROID,
    ): Single<GetAccessTokenResponseData>
}

private const val SAFETY_TOKEN_HEADER = "Safety-Token"
private const val USER_AGENT_HEADER = "User-Agent"
private const val USER_AGENT_ANDROID = "android"
