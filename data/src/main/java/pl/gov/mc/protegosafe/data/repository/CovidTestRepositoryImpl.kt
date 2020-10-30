package pl.gov.mc.protegosafe.data.repository

import android.os.Build
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.data.cloud.CovidTestService
import pl.gov.mc.protegosafe.data.mapper.toEntity
import pl.gov.mc.protegosafe.data.model.covidtest.CreateTestSubscriptionRequestData
import pl.gov.mc.protegosafe.data.db.dao.CovidTestDao
import pl.gov.mc.protegosafe.data.mapper.toTestSubscriptionDto
import pl.gov.mc.protegosafe.data.model.covidtest.TestSubscriptionCreateRequest
import pl.gov.mc.protegosafe.data.model.covidtest.TestSubscriptionStatusRequest
import pl.gov.mc.protegosafe.data.model.covidtest.TestSubscriptionStatusRequestBody
import pl.gov.mc.protegosafe.domain.model.TestSubscriptionStatus
import pl.gov.mc.protegosafe.domain.model.TestSubscriptionItem
import pl.gov.mc.protegosafe.domain.repository.CovidTestRepository
import pl.gov.mc.protegosafe.domain.repository.SafetyNetRepository
import java.util.UUID

class CovidTestRepositoryImpl(
    private val covidTestDao: CovidTestDao,
    private val covidTestService: CovidTestService,
    private val safetyNetRepository: SafetyNetRepository,
) : CovidTestRepository {

    override fun getTestSubscription(testPin: String, guid: String): Single<TestSubscriptionItem> {
        return getSafetynetToken()
            .observeOn(Schedulers.io())
            .map { safetynetToken ->
                TestSubscriptionCreateRequest(
                    safetynetToken = safetynetToken,
                    createTestSubscriptionRequestData = CreateTestSubscriptionRequestData(
                        code = testPin,
                        guid = guid
                    )
                )
            }
            .flatMap { createSubscriptionRequest ->
                covidTestService.createSubscription(
                    createSubscriptionRequest.safetynetToken,
                    createSubscriptionRequest.createTestSubscriptionRequestData
                )
            }.map {
                TestSubscriptionItem(
                    guid = guid,
                    accessToken = it.token
                )
            }
    }

    override fun updateTestSubscriptionStatus(
        testSubscription: TestSubscriptionItem
    ): Single<TestSubscriptionItem> {
        return getSafetynetToken()
            .observeOn(Schedulers.io())
            .map { safetynetToken ->
                TestSubscriptionStatusRequest(
                    accessToken = testSubscription.accessToken,
                    safetynetToken = safetynetToken,
                    testSubscriptionStatusRequestBody = TestSubscriptionStatusRequestBody(
                        testSubscription.guid
                    )
                )
            }
            .observeOn(Schedulers.io())
            .flatMap {
                covidTestService.getSubscriptionStatus(
                    accessToken = it.accessToken,
                    safetynetToken = it.safetynetToken,
                    testSubscriptionStatusRequestBody = it.testSubscriptionStatusRequestBody
                )
            }.map {
                TestSubscriptionItem(
                    guid = it.guid,
                    accessToken = testSubscription.accessToken,
                    status = TestSubscriptionStatus.valueOf(it.status)
                )
            }
    }

    override fun saveTestSubscription(testSubscriptionItem: TestSubscriptionItem): Completable {
        return covidTestDao.updateTestSubscription(testSubscriptionItem.toTestSubscriptionDto())
    }

    override fun getTestSubscription(): Maybe<TestSubscriptionItem> {
        return covidTestDao.getTestSubscription()
            .map { it.toEntity() }
    }

    override fun getTestSubscriptionPin(): Single<String> {
        return covidTestDao.getTestSubscriptionPin()
            .map { it.testPin }
    }

    override fun saveTestSubscriptionPin(testPin: String): Completable {
        return covidTestDao.updateTestPin(testPin)
    }

    override fun clearCovidTestData(): Completable {
        return covidTestDao.clearCovidTestData()
    }

    override fun isDeviceCompatible(): Single<Boolean> {
        return Single.fromCallable {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        }
    }

    private fun getSafetynetToken(): Single<String> {
        return safetyNetRepository.generateNonce(UUID.randomUUID().toString())
            .flatMap {
                safetyNetRepository.getTokenFor(it)
            }
    }
}
