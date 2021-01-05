package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.ExposureItem
import pl.gov.mc.protegosafe.domain.repository.ExposureRepository

class SaveExposureUseCase(
    private val exposureRepository: ExposureRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(exposure: ExposureItem): Completable {
        return exposureRepository.upsert(exposure)
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}
