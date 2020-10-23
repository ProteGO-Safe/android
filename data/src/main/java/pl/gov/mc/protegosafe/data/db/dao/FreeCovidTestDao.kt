package pl.gov.mc.protegosafe.data.db.dao

import doTransaction
import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.data.model.freecovidtest.TestSubscriptionDto
import pl.gov.mc.protegosafe.data.model.freecovidtest.TestSubscriptionPinDto
import singleQuery

open class FreeCovidTestDao {

    /**
     * If exists in database just return [TestSubscriptionDto] object
     * else return new [TestSubscriptionDto] with default values and save it into database.
     * @return [TestSubscriptionDto]
     */
    fun getTestSubscription(): Single<TestSubscriptionDto> {
        return singleQuery<TestSubscriptionDto>()
            .map {
                if (it.isEmpty()) {
                    TestSubscriptionDto()
                } else {
                    it.first()
                }
            }
            .flatMap { subscriptionDto ->
                doTransaction {
                    it.copyToRealmOrUpdate(subscriptionDto)
                }.toSingle {
                    subscriptionDto
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
            .map { it.firstOrNull() }
    }

    fun updateTestPin(testSubscriptionPin: TestSubscriptionPinDto): Completable {
        return doTransaction {
            it.copyToRealmOrUpdate(testSubscriptionPin)
        }
    }
}
