package pl.gov.mc.protegosafe.helpers

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.repository.DiagnosisKeyRepository
import pl.gov.mc.protegosafe.domain.scheduler.ApplicationTaskScheduler

class GetAllTeksAndAnalyzeUseCase(
    private val applicationTaskScheduler: ApplicationTaskScheduler,
    private val diagnosisKeyRepository: DiagnosisKeyRepository,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(): Single<String> {
        return cancelProvideDiagnosisKeysTask()
            .andThen(
                diagnosisKeyRepository.setLatestProcessedDiagnosisKeyTimestamp(
                    TIMESTAMP_TO_GET_ALL_FILES
                ).andThen(
                    Completable.defer {
                        scheduleProvideDiagnosisKeysTask()
                    }
                )
            )
            .toSingle {
                "Make sure EN is enabled, please wait for analyze result"
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun cancelProvideDiagnosisKeysTask(): Completable {
        return Completable.fromAction {
            applicationTaskScheduler.cancelProvideDiagnosisKeysTask()
        }
    }

    private fun scheduleProvideDiagnosisKeysTask(): Completable {
        return Completable.fromAction {
            applicationTaskScheduler.scheduleProvideDiagnosisKeysTask()
        }
    }
}

private const val TIMESTAMP_TO_GET_ALL_FILES = 1L
