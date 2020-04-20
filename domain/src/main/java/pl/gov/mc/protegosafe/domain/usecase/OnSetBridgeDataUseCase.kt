package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.TriageRepository
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.IncomingBridgeDataItem
import pl.gov.mc.protegosafe.domain.model.IncomingBridgeDataType

class OnSetBridgeDataUseCase(
    private val postExecutionThread: PostExecutionThread,
    private val triageRepository: TriageRepository
) {

    fun execute(input: IncomingBridgeDataItem): Completable = Completable.fromAction {
        when (input.type) {
            IncomingBridgeDataType.TRIAGE -> {
                val data = triageRepository.parseBridgePayload(input.payload)
                triageRepository.saveTriageCompletedTimestamp(data.timestamp)
            }
        }
    }
        .subscribeOn(Schedulers.io())
        .observeOn(postExecutionThread.scheduler)
}