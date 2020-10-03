package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.ExposureItem
import pl.gov.mc.protegosafe.domain.repository.ExposureRepository

class SaveMatchedTokenUseCase(
    private val exposureRepository: ExposureRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(exposure: ExposureItem): Completable {
        return exposureRepository.upsert(exposure)
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}
