package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.ActionRequiredItem
import pl.gov.mc.protegosafe.domain.model.IncomingBridgePayloadMapper

class CloseAppUseCase(
    private val incomingBridgePayloadMapper: IncomingBridgePayloadMapper,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(
        payload: String,
        onResultActionRequired: (ActionRequiredItem) -> Unit
    ): Completable {
        return Single.fromCallable { incomingBridgePayloadMapper.toCloseAppItem(payload) }
            .flatMapCompletable {
                if (it.turnOff) {
                    Completable.create {
                        onResultActionRequired(ActionRequiredItem.CloseApp)
                    }
                } else {
                    Completable.complete()
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}
