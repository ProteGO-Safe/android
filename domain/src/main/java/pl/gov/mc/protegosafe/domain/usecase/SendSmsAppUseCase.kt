package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.ActionRequiredItem
import pl.gov.mc.protegosafe.domain.model.IncomingBridgePayloadMapper
import pl.gov.mc.protegosafe.domain.model.SendSmsItem

class SendSmsAppUseCase(
    private val incomingBridgePayloadMapper: IncomingBridgePayloadMapper,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(
        payload: String,
        onResultActionRequired: (ActionRequiredItem) -> Unit
    ): Completable {
        return Single.fromCallable { incomingBridgePayloadMapper.toSendSmsItem(payload) }
            .flatMapCompletable { smsItem -> sendOpenSmsAppAction(smsItem, onResultActionRequired) }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun sendOpenSmsAppAction(
        sendSmsItem: SendSmsItem,
        onResultActionRequired: (ActionRequiredItem) -> Unit
    ): Completable {
        return Completable.fromAction {
            val action = ActionRequiredItem.OpenSmsApp(
                number = sendSmsItem.number,
                text = sendSmsItem.text
            )
            onResultActionRequired(action)
        }
    }
}
