package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.Date
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.extension.getExposureLastValidDate
import pl.gov.mc.protegosafe.domain.model.ExposureItem
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.repository.ExposureRepository

class GetAnalyzeResultUseCase(
    private val exposureRepository: ExposureRepository,
    private val postExecutionThread: PostExecutionThread,
    private val resultComposer: OutgoingBridgeDataResultComposer
) {
    fun execute(): Single<String> {
        return exposureRepository.getAllResults()
            .map { listOfExposures ->
                resultComposer.composeAnalyzeResult(
                    listOfExposures
                        .filter { !Date(it.date).before(getExposureLastValidDate()) }
                        .maxBy { it.riskScore }
                        ?: ExposureItem(
                            System.currentTimeMillis(),
                            NO_EXPOSURE_DEFAULT_VALUE,
                            NO_EXPOSURE_DEFAULT_VALUE
                        ))
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}

private const val NO_EXPOSURE_DEFAULT_VALUE = 0
