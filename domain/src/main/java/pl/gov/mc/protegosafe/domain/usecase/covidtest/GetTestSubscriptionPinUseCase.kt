package pl.gov.mc.protegosafe.domain.usecase.covidtest

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.repository.CovidTestRepository

class GetTestSubscriptionPinUseCase(
    private val covidTestRepository: CovidTestRepository,
    private val resultComposer: OutgoingBridgeDataResultComposer,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(): Single<String> {
        return covidTestRepository.getTestSubscriptionPin()
            .flatMap {
                getResult(it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun getResult(pin: String): Single<String> {
        return Single.fromCallable {
            resultComposer.composeTestSubscriptionPinResult(pin)
        }
    }
}
