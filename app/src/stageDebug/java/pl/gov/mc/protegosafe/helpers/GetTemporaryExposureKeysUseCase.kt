package pl.gov.mc.protegosafe.helpers

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeyItem
import pl.gov.mc.protegosafe.domain.repository.ExposureNotificationRepository
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread

class GetTemporaryExposureKeysUseCase(
    private val exposureNotificationRepository: ExposureNotificationRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(): Single<List<TemporaryExposureKeyItem>> {
        return exposureNotificationRepository.getTemporaryExposureKeyHistory()
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}
