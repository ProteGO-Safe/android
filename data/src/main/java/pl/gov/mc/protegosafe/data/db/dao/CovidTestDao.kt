package pl.gov.mc.protegosafe.data.db.dao

import doTransaction
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.data.model.covidtest.TestSubscriptionDto
import pl.gov.mc.protegosafe.data.model.covidtest.TestSubscriptionPinDto
import singleQuery
import timber.log.Timber

open class CovidTestDao {

    fun getTestSubscription(): Maybe<TestSubscriptionDto> {
        return singleQuery<TestSubscriptionDto>()
            .observeOn(Schedulers.io())
            .flatMapMaybe { list ->
                Maybe.create { emitter ->
                    if (list.isEmpty()) {
                        emitter.onComplete()
                    } else {
                        emitter.onSuccess(
                            list.first()
                        )
                    }
                }
            }
    }

    fun updateTestSubscription(testSubscription: TestSubscriptionDto): Completable {
        return doTransaction {
            it.copyToRealmOrUpdate(testSubscription)
        }
    }

    fun getTestSubscriptionPin(): Single<TestSubscriptionPinDto> {
        return singleQuery<TestSubscriptionPinDto>()
            .map { it.firstOrNull() ?: TestSubscriptionPinDto() }
    }

    fun updateTestPin(testPin: String): Completable {
        return doTransaction {
            it.copyToRealmOrUpdate(TestSubscriptionPinDto(testPin = testPin))
        }
    }

    fun clearCovidTestData(): Completable {
        Timber.d("Clearing all Covid Test data")
        return removeTestSubscription()
            .andThen(removeTestSubscriptionPin())
    }

    private fun removeTestSubscription(): Completable {
        return doTransaction {
            it.where(TestSubscriptionDto::class.java).findAll().forEach { subscriptionDto ->
                subscriptionDto.deleteFromRealm()
            }
        }
    }

    private fun removeTestSubscriptionPin(): Completable {
        return doTransaction {
            it.where(TestSubscriptionPinDto::class.java).findAll()
                .forEach { subscriptionPinDto ->
                    subscriptionPinDto.deleteFromRealm()
                }
        }
    }
}
