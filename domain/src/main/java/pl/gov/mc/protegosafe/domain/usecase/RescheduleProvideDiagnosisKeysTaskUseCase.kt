package pl.gov.mc.protegosafe.domain.usecase

import pl.gov.mc.protegosafe.domain.repository.WorkerStateRepository
import pl.gov.mc.protegosafe.domain.scheduler.ApplicationTaskScheduler

class RescheduleProvideDiagnosisKeysTaskUseCase(
    private val workerStateRepository: WorkerStateRepository,
    private val applicationTaskScheduler: ApplicationTaskScheduler
) {
    fun execute() {
        if (workerStateRepository.shouldProvideDiagnosisKeysWorkerStartOnBoot) {
            applicationTaskScheduler.scheduleProvideDiagnosisKeysTask(false)
        }
    }
}
