package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import pl.gov.mc.protegosafe.domain.repository.ExposureNotificationRepository
import pl.gov.mc.protegosafe.domain.repository.WorkerStateRepository
import pl.gov.mc.protegosafe.domain.scheduler.ApplicationTaskScheduler

class StopExposureNotificationUseCase(
    private val workerStateRepository: WorkerStateRepository,
    private val exposureNotificationRepository: ExposureNotificationRepository,
    private val applicationTaskScheduler: ApplicationTaskScheduler
) {
    fun execute(): Completable {
        return exposureNotificationRepository.stop()
            .doOnComplete {
                applicationTaskScheduler.cancelProvideDiagnosisKeysTask()
                workerStateRepository.shouldProvideDiagnosisKeysWorkerStartOnBoot = false
            }
    }
}
