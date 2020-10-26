package pl.gov.mc.protegosafe.data.cloud

import io.reactivex.Single
import pl.gov.mc.protegosafe.data.BuildConfig
import pl.gov.mc.protegosafe.data.model.GetAccessTokenResponseData
import pl.gov.mc.protegosafe.data.model.covidtest.CreateTestSubscriptionRequestData
import pl.gov.mc.protegosafe.data.model.covidtest.SubscriptionStatusResponseData
import pl.gov.mc.protegosafe.data.model.covidtest.TestSubscriptionStatusRequestBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface CovidTestService {

    @POST("${BuildConfig.COVID_TEST_URL}createSubscription")
    fun createSubscription(
        @Header(SAFETY_TOKEN_HEADER) safetynetToken: String,
        @Body createTestSubscriptionRequestData: CreateTestSubscriptionRequestData,
        @Header(USER_AGENT_HEADER) userAgent: String = USER_AGENT_ANDROID
    ): Single<GetAccessTokenResponseData>

    @POST("${BuildConfig.COVID_TEST_URL}getSubscription")
    fun getSubscriptionStatus(
        @Header(AUTHORIZATION_HEADER) accessToken: String,
        @Header(SAFETY_TOKEN_HEADER) safetynetToken: String,
        @Body testSubscriptionStatusRequestBody: TestSubscriptionStatusRequestBody,
        @Header(USER_AGENT_HEADER) userAgent: String = USER_AGENT_ANDROID
    ): Single<SubscriptionStatusResponseData>
}

private const val SAFETY_TOKEN_HEADER = "Safety-Token"
private const val USER_AGENT_HEADER = "User-Agent"
private const val USER_AGENT_ANDROID = "android"
private const val AUTHORIZATION_HEADER = "Authorization"
