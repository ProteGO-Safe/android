package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable
import io.reactivex.Single

interface FreeCovidTestRepository {
    fun getTestSubscriptionAccessToken(testPin: String): Single<String>
    fun saveTestSubscriptionAccessToken(accessToken: String): Completable
    fun getTestSubscriptionPin(): Single<String>
    fun updateTestSubscriptionPin(testPin: String): Completable
}
