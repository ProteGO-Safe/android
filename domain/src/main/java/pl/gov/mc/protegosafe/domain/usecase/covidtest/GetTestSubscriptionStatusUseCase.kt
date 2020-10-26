package pl.gov.mc.protegosafe.domain.usecase.covidtest

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.extension.getCurrentTimeInSeconds
import pl.gov.mc.protegosafe.domain.model.ActionRequiredItem
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.model.TestSubscriptionConfigurationItem
import pl.gov.mc.protegosafe.domain.model.TestSubscriptionItem
import pl.gov.mc.protegosafe.domain.repository.CovidTestRepository
import pl.gov.mc.protegosafe.domain.repository.RemoteConfigurationRepository

class GetTestSubscriptionStatusUseCase(
    private val covidTestRepository: CovidTestRepository,
    private val resultComposer: OutgoingBridgeDataResultComposer,
    private val remoteConfigurationRepository: RemoteConfigurationRepository,
    private val postExecutionThread: PostExecutionThread,
) {
    fun execute(onResultActionRequired: (ActionRequiredItem) -> Unit): Single<String> {
        return covidTestRepository.getTestSubscription()
            .flatMapSingle { testSubscription ->
                callUpdateTestSubscriptionResultRequiredIfNecessary(
                    testSubscription,
                    onResultActionRequired
                )
                    .andThen(getResult(testSubscription))
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

    private fun callUpdateTestSubscriptionResultRequiredIfNecessary(
        testSubscription: TestSubscriptionItem,
        onResultActionRequired: (ActionRequiredItem) -> Unit
    ): Completable {
        return getUpdateSubscriptionConfiguration()
            .flatMapCompletable {
                if (getCurrentTimeInSeconds() - testSubscription.updated > it.interval) {
                    Completable.fromAction {
                        onResultActionRequired(ActionRequiredItem.UpdateTestSubscription)
                    }
                } else {
                    Completable.complete()
                }
            }
    }

    private fun getUpdateSubscriptionConfiguration(): Single<TestSubscriptionConfigurationItem> {
        return remoteConfigurationRepository.update()
            .andThen(remoteConfigurationRepository.getTestSubscriptionConfiguration())
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
