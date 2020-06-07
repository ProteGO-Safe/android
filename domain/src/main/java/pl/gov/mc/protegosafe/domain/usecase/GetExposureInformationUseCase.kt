package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.ExposureInformationItem
import pl.gov.mc.protegosafe.domain.repository.ExposureNotificationRepository

class GetExposureInformationUseCase(
    private val exposureNotificationRepository: ExposureNotificationRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(token: String): Single<List<ExposureInformationItem>> {
        return exposureNotificationRepository.getExposureInformation(token)
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}
