package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import pl.gov.mc.protegosafe.domain.repository.ExposureNotificationRepository
import pl.gov.mc.protegosafe.domain.repository.WorkerStateRepository
import pl.gov.mc.protegosafe.domain.scheduler.ApplicationTaskScheduler

class StartExposureNotificationUseCase(
    private val workerStateRepository: WorkerStateRepository,
    private val exposureNotificationRepository: ExposureNotificationRepository,
    private val applicationTaskScheduler: ApplicationTaskScheduler
) {

    fun execute(): Completable {
        return exposureNotificationRepository.start()
            .doOnComplete {
                applicationTaskScheduler.scheduleProvideDiagnosisKeysTask()
                workerStateRepository.shouldProvideDiagnosisKeysWorkerStartOnBoot = true
            }
    }
}
