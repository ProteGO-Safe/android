package pl.gov.mc.protegosafe.domain.usecase.covidtest

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.model.TestSubscriptionItem
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
                saveTestSubscriptionAndGetResult(updatedSubscription)
            }
    }

    private fun saveTestSubscriptionAndGetResult(testSubscription: TestSubscriptionItem): Single<String> {
        return covidTestRepository.saveTestSubscription(testSubscription)
            .andThen(getResult(testSubscription))
    }

    private fun getResult(testSubscriptionItem: TestSubscriptionItem?): Single<String> {
        return Single.fromCallable {
            resultComposer.composeTestSubscriptionStatusResult(testSubscriptionItem)
        }
    }
}
