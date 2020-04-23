package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.repository.TriageRepository
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.IncomingBridgeDataItem
import pl.gov.mc.protegosafe.domain.model.IncomingBridgeDataType
import pl.gov.mc.protegosafe.domain.model.TraceStatusMapper
import java.lang.IllegalStateException

class OnSetBridgeDataUseCase(
    private val postExecutionThread: PostExecutionThread,
    private val triageRepository: TriageRepository,
    private val startBLEMonitoringServiceUseCase: StartBLEMonitoringServiceUseCase,
    private val stopBLEMonitoringServiceUseCase: StopBLEMonitoringServiceUseCase,
    private val mapper: TraceStatusMapper
    ) {

    fun execute(input: IncomingBridgeDataItem): Completable = Completable.fromAction {
        when (input.type) {
            IncomingBridgeDataType.TRIAGE -> {
                val data = triageRepository.parseBridgePayload(input.payload)
                triageRepository.saveTriageCompletedTimestamp(data.timestamp)
            }
            IncomingBridgeDataType.REQUEST_TRACE_SERVICE_CHANGE -> {
                val data = mapper.toDomainItem(input.payload)
                if (data.enableBtService) {
                    println("WNASILOWSKILOG enable")
                    startBLEMonitoringServiceUseCase.execute()
                } else {
                    println("WNASILOWSKILOG disable")
                    stopBLEMonitoringServiceUseCase.execute()
                }
            }
            else -> throw IllegalStateException("Illegal input type")
        }
    }
        .subscribeOn(Schedulers.io())
        .observeOn(postExecutionThread.scheduler)
}