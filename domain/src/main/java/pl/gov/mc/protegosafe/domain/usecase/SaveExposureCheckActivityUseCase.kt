package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.ExposureCheckActivityItem
import pl.gov.mc.protegosafe.domain.model.ExposureItem
import pl.gov.mc.protegosafe.domain.repository.ActivitiesRepository
import pl.gov.mc.protegosafe.domain.repository.ExposureRepository

class SaveExposureCheckActivityUseCase(
    private val activitiesRepository: ActivitiesRepository,
    private val exposureRepository: ExposureRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(exposures: List<ExposureItem>): Completable {
        return exposureRepository.getMaxExposureOrDefault(exposures)
            .flatMap {
                exposureRepository.calcRiskLevel(it)
            }
            .map { riskLevel ->
                ExposureCheckActivityItem(
                    riskLevel = riskLevel,
                    exposures = exposures.size
                )
            }
            .flatMapCompletable {
                activitiesRepository.saveExposureCheckActivity(it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}
