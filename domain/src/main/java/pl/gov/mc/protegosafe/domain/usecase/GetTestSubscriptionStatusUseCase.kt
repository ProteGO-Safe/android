package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.model.TestSubscriptionItem
import pl.gov.mc.protegosafe.domain.repository.CovidTestRepository

class GetTestSubscriptionStatusUseCase(
    private val covidTestRepository: CovidTestRepository,
    private val resultComposer: OutgoingBridgeDataResultComposer,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(): Single<String> {
        return covidTestRepository.getTestSubscription()
            .flatMapSingle { testSubscription ->
                covidTestRepository.updateTestSubscriptionStatus(testSubscription)
                    .flatMap { updatedSubscription ->
                        saveTestSubscriptionAndGetResult(updatedSubscription)
                    }
            }
            .onErrorResumeNext {
                if (it is NoSuchElementException) {
                    getTestSubscriptionNotExistsResult()
                } else {
                    Single.error(it)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun saveTestSubscriptionAndGetResult(testSubscription: TestSubscriptionItem): Single<String> {
        return covidTestRepository.saveTestSubscription(testSubscription)
            .andThen(getResult(testSubscription))
    }

    private fun getTestSubscriptionNotExistsResult(): Single<String> {
        return getResult(null)
    }

    private fun getResult(testSubscriptionItem: TestSubscriptionItem?): Single<String> {
        return Single.fromCallable {
            resultComposer.composeTestSubscriptionStatusResult(testSubscriptionItem)
        }
    }
}
