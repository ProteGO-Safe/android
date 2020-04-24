package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.IncomingBridgeDataItem
import pl.gov.mc.protegosafe.domain.model.IncomingBridgeDataType
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataType
import pl.gov.mc.protegosafe.domain.model.TraceStatusMapper
import pl.gov.mc.protegosafe.domain.repository.TriageRepository

class OnSetBridgeDataUseCase(
    private val postExecutionThread: PostExecutionThread,
    private val triageRepository: TriageRepository,
    private val enableEnableBTServiceUseCase: EnableBTServiceUseCase,
    private val servicesStatusUseCase: GetServicesStatusUseCase,
    private val mapper: TraceStatusMapper
    ) {

    fun execute(input: IncomingBridgeDataItem, onBridgeData: (Int, String) -> Unit): Completable = Completable.fromAction {
        when (input.type) {
            IncomingBridgeDataType.TRIAGE -> {
                val data = triageRepository.parseBridgePayload(input.payload)
                triageRepository.saveTriageCompletedTimestamp(data.timestamp)
            }
            IncomingBridgeDataType.REQUEST_ENABLE_BT_SERVICE -> {
                val data = mapper.toDomainItem(input.payload)
                enableEnableBTServiceUseCase.execute(data.enableBtService)
                onBridgeData(
                    OutgoingBridgeDataType.SERVICE_STATUS_CHANGE.code,
                    servicesStatusUseCase.execute()
                )
            }
            else -> throw IllegalStateException("Illegal input type")
        }
    }
        .subscribeOn(Schedulers.io())
        .observeOn(postExecutionThread.scheduler)
}