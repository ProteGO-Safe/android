package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.repository.ExposureRepository

class CancelExposureRiskUseCase(
    private val exposureRepository: ExposureRepository,
    private val analyzeResultUseCase: GetAnalyzeResultUseCase,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(): Single<String> {
        return exposureRepository.nukeDb()
            .andThen(analyzeResultUseCase.execute())
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}
