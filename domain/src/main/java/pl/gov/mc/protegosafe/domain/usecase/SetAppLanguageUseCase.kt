package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.ActionRequiredItem
import pl.gov.mc.protegosafe.domain.model.IncomingBridgePayloadMapper
import pl.gov.mc.protegosafe.domain.repository.AppRepository

class SetAppLanguageUseCase(
    private val appRepository: AppRepository,
    private val incomingBridgePayloadMapper: IncomingBridgePayloadMapper,
    private val postExecutionThread: PostExecutionThread
) {

    fun execute(payload: String, onResultActionRequired: (ActionRequiredItem) -> Unit): Completable {
        return Single.fromCallable { incomingBridgePayloadMapper.toLanguageISO(payload) }
            .flatMapCompletable {
                appRepository.setAppLanguage(it)
            }
            .andThen {
                onResultActionRequired(ActionRequiredItem.RestartActivity)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}
