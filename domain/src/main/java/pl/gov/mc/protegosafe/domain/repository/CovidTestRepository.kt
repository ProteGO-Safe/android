package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable
import io.reactivex.Single

interface CovidTestRepository {
    /**
     * Upload TEST PIN to get subscription access token
     * @return [String] accessToken
     */
    fun getTestSubscriptionAccessToken(testPin: String): Single<String>

    /**
     * Create or update Subscription access token in realm database
     */
    fun saveTestSubscriptionAccessToken(accessToken: String): Completable

    /**
     * @return [String] subscription TEST PIN or empty [String] as default value
     */
    fun getTestSubscriptionPin(): Single<String>
    /**
     * Create or update Subscription TEST PIN in realm database
     */
    fun saveTestSubscriptionPin(testPin: String): Completable
}
