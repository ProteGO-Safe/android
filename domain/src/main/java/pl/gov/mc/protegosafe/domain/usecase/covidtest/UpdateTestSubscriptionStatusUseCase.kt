package pl.gov.mc.protegosafe.domain.usecase.covidtest

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.model.TestSubscriptionItem
import pl.gov.mc.protegosafe.domain.model.TestSubscriptionStatus
import pl.gov.mc.protegosafe.domain.repository.CovidTestRepository

class UpdateTestSubscriptionStatusUseCase(
    private val covidTestRepository: CovidTestRepository,
    private val resultComposer: OutgoingBridgeDataResultComposer,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(): Single<String> {
        return covidTestRepository.getTestSubscription()
            .flatMapSingle {
                updateTestSubscription(it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun updateTestSubscription(testSubscription: TestSubscriptionItem): Single<String> {
        return covidTestRepository.updateTestSubscriptionStatus(testSubscription)
            .flatMap { updatedSubscription ->
                clearTestSubscriptionPinIfVerified(updatedSubscription)
                    .andThen(saveTestSubscriptionAndGetResult(updatedSubscription))
            }
    }

    private fun saveTestSubscriptionAndGetResult(testSubscription: TestSubscriptionItem): Single<String> {
        return covidTestRepository.saveTestSubscription(testSubscription)
            .andThen(getResult(testSubscription))
    }

    private fun clearTestSubscriptionPinIfVerified(
        testSubscription: TestSubscriptionItem
    ): Completable {
        return if (testSubscription.status == TestSubscriptionStatus.SCHEDULED) {
            covidTestRepository.saveTestSubscriptionPin("")
        } else {
            Completable.complete()
        }
    }

    private fun getResult(testSubscriptionItem: TestSubscriptionItem?): Single<String> {
        return Single.fromCallable {
            resultComposer.composeTestSubscriptionStatusResult(testSubscriptionItem)
        }
    }
}
