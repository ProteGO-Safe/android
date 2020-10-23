package pl.gov.mc.protegosafe.data.repository

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.data.cloud.FreeCovidTestService
import pl.gov.mc.protegosafe.data.mapper.toEntity
import pl.gov.mc.protegosafe.data.model.freecovidtest.TestSubscriptionRequestData
import pl.gov.mc.protegosafe.data.db.dao.FreeCovidTestDao
import pl.gov.mc.protegosafe.domain.model.TestSubscriptionItem
import pl.gov.mc.protegosafe.domain.repository.FreeCovidTestRepository
import pl.gov.mc.protegosafe.domain.repository.SafetyNetRepository
import java.util.UUID

class FreeCovidTestRepositoryImpl(
    private val freeCovidTestDao: FreeCovidTestDao,
    private val freeCovidTestService: FreeCovidTestService,
    private val safetyNetRepository: SafetyNetRepository,
) : FreeCovidTestRepository {

    override fun getTestSubscriptionAccessToken(testPin: String): Single<String> {
        return getTestSubscription()
            .zipWith(getSafetynetToken()) { testSubscription, safetynetToken ->
                CreateSubscriptionRequest(
                    safetynetToken = safetynetToken,
                    testSubscriptionRequestData = getCreateSubscriptionRequestData(
                        testPin = testPin,
                        guid = testSubscription.guid
                    )
                )
            }
            .observeOn(Schedulers.io())
            .flatMap { createSubscriptionRequest ->
                freeCovidTestService.createSubscription(
                    createSubscriptionRequest.safetynetToken,
                    createSubscriptionRequest.testSubscriptionRequestData
                )
            }.map {
                it.token
            }
    }

    override fun saveTestSubscriptionAccessToken(accessToken: String): Completable {
        return freeCovidTestDao.getTestSubscription()
            .map {
                it.accessToken = accessToken
                it.updated = System.currentTimeMillis()

                it
            }
            .flatMapCompletable {
                freeCovidTestDao.updateTestSubscription(it)
            }
    }

    override fun getTestSubscriptionPin(): Single<String> {
        return freeCovidTestDao.getTestSubscriptionPin()
            .map { it.testPin }
    }

    override fun updateTestSubscriptionPin(testPin: String): Completable {
        return freeCovidTestDao.getTestSubscriptionPin()
            .map {
                it.testPin = testPin

                it
            }
            .flatMapCompletable {
                freeCovidTestDao.updateTestPin(it)
            }
    }

    private fun getTestSubscription(): Single<TestSubscriptionItem> {
        return freeCovidTestDao.getTestSubscription()
            .map { it.toEntity() }
    }

    private fun getSafetynetToken(): Single<String> {
        return safetyNetRepository.generateNonce(UUID.randomUUID().toString())
            .flatMap {
                safetyNetRepository.getTokenFor(it)
            }
    }

    private fun getCreateSubscriptionRequestData(
        testPin: String,
        guid: String
    ): TestSubscriptionRequestData {
        return TestSubscriptionRequestData(guid, testPin)
    }

    inner class CreateSubscriptionRequest(
        val safetynetToken: String,
        val testSubscriptionRequestData: TestSubscriptionRequestData
    )
}
