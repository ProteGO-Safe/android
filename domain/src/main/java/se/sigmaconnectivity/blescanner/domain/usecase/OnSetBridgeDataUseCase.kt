package se.sigmaconnectivity.blescanner.domain.usecase

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import se.sigmaconnectivity.blescanner.domain.TriageRepository
import se.sigmaconnectivity.blescanner.domain.executor.PostExecutionThread
import se.sigmaconnectivity.blescanner.domain.model.IncomingBridgeDataItem
import se.sigmaconnectivity.blescanner.domain.model.IncomingBridgeDataType

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