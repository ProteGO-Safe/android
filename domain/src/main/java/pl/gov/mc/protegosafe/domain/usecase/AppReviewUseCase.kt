package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.ActionRequiredItem
import pl.gov.mc.protegosafe.domain.model.IncomingBridgePayloadMapper

class AppReviewUseCase(
    private val incomingBridgePayloadMapper: IncomingBridgePayloadMapper,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(
        payload: String,
        onResultActionRequired: (ActionRequiredItem) -> Unit
    ): Completable {
        return Single.fromCallable { incomingBridgePayloadMapper.toAppReviewItem(payload) }
            .flatMapCompletable {
                if (it.appReview) {
                    sendAppReviewAction(onResultActionRequired)
                } else {
                    Completable.complete()
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun sendAppReviewAction(
        onResultActionRequired: (ActionRequiredItem) -> Unit
    ): Completable {
        return Completable.fromAction {
            onResultActionRequired(ActionRequiredItem.AppReview)
        }
    }
}
