package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.model.TestSubscriptionItem

interface CovidTestRepository {
    /**
     * Upload TEST PIN to get subscription access token
     * @return [String] accessToken
     */
    fun getTestSubscription(testPin: String, guid: String): Single<TestSubscriptionItem>
    /**
     * @return [String] subscription TEST PIN or empty [String] as default value
     */
    fun getTestSubscriptionPin(): Single<String>

    /**
     * Create or update Subscription TEST PIN in realm database
     */
    fun saveTestSubscriptionPin(testPin: String): Completable

    fun updateTestSubscriptionStatus(testSubscription: TestSubscriptionItem): Single<TestSubscriptionItem>

    fun saveTestSubscription(testSubscriptionItem: TestSubscriptionItem): Completable

    fun getTestSubscription(): Maybe<TestSubscriptionItem>

    fun clearCovidTestData(): Completable

    fun isDeviceCompatible(): Single<Boolean>
}
