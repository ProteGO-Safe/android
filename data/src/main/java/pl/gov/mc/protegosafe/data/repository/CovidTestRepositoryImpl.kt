package pl.gov.mc.protegosafe.data.repository

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.data.cloud.CovidTestService
import pl.gov.mc.protegosafe.data.mapper.toEntity
import pl.gov.mc.protegosafe.data.model.covidtest.TestSubscriptionRequestData
import pl.gov.mc.protegosafe.data.db.dao.CovidTestDao
import pl.gov.mc.protegosafe.domain.model.TestSubscriptionItem
import pl.gov.mc.protegosafe.domain.repository.CovidTestRepository
import pl.gov.mc.protegosafe.domain.repository.SafetyNetRepository
import java.util.UUID

class CovidTestRepositoryImpl(
    private val covidTestDao: CovidTestDao,
    private val covidTestService: CovidTestService,
    private val safetyNetRepository: SafetyNetRepository,
) : CovidTestRepository {

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
                covidTestService.createSubscription(
                    createSubscriptionRequest.safetynetToken,
                    createSubscriptionRequest.testSubscriptionRequestData
                )
            }.map {
                it.token
            }
    }

    override fun saveTestSubscriptionAccessToken(accessToken: String): Completable {
        return covidTestDao.getTestSubscription()
            .map {
                it.accessToken = accessToken
                it.updated = System.currentTimeMillis()

                it
            }
            .flatMapCompletable {
                covidTestDao.updateTestSubscription(it)
            }
    }

    override fun getTestSubscriptionPin(): Single<String> {
        return covidTestDao.getTestSubscriptionPin()
            .map { it.testPin }
    }

    override fun updateTestSubscriptionPin(testPin: String): Completable {
        return covidTestDao.getTestSubscriptionPin()
            .map {
                it.testPin = testPin

                it
            }
            .flatMapCompletable {
                covidTestDao.updateTestPin(it)
            }
    }

    override fun clearCovidTestData(): Completable {
        return covidTestDao.clearCovidTestData()
    }

    private fun getTestSubscription(): Single<TestSubscriptionItem> {
        return covidTestDao.getTestSubscription()
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
