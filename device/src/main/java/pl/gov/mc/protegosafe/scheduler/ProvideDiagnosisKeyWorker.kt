package pl.gov.mc.protegosafe.scheduler

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.File
import org.koin.core.KoinComponent
import org.koin.core.inject
import pl.gov.mc.protegosafe.domain.model.ExposureConfigurationItem
import pl.gov.mc.protegosafe.domain.repository.DiagnosisKeyRepository
import pl.gov.mc.protegosafe.domain.repository.ExposureNotificationRepository
import pl.gov.mc.protegosafe.domain.repository.RemoteConfigurationRepository
import pl.gov.mc.protegosafe.domain.usecase.DiagnosisKeysFileNameToTimestampUseCase
import pl.gov.mc.protegosafe.domain.usecase.ProvideDiagnosisKeysUseCase
import timber.log.Timber

class ProvideDiagnosisKeyWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : RxWorker(appContext, workerParameters), KoinComponent {

    private val exposureNotificationRepository: ExposureNotificationRepository by inject()
    private val provideDiagnosisKeysUseCase: ProvideDiagnosisKeysUseCase by inject()
    private val remoteConfigurationRepository: RemoteConfigurationRepository by inject()
    private val diagnosisKeyRepository: DiagnosisKeyRepository by inject()

    override fun createWork(): Single<Result> {
        return exposureNotificationRepository.isEnabled().flatMap { enabled ->
            Timber.i("ProvideDiagnosisKeyWorker run.")
            return@flatMap if (!enabled) {
                Timber.w("Exposure notifications isn't running")
                Single.just(Result.failure())
            } else {
                diagnosisKeyRepository.getDiagnosisKeys(
                    diagnosisKeyRepository.getLatestProcessedDiagnosisKeyTimestamp()
                ).flatMap { diagnosisKeyFiles ->
                    if (diagnosisKeyFiles.isEmpty()) {
                        Timber.i("No new diagnosis keys to provide.")
                        Single.just(Result.success())
                    } else {
                        Timber.d("There are new diagnosis keys to provide: $diagnosisKeyFiles")
                        getExposureConfiguration().flatMap { exposureConfiguration ->
                            Timber.d("getExposureConfiguration() = $exposureConfiguration")
                            provideDiagnosisKeysUseCase.execute(
                                files = diagnosisKeyFiles,
                                exposureConfigurationItem = exposureConfiguration
                            ).doOnComplete {
                                finalizeDiagnosisKeyProviding(diagnosisKeyFiles)
                            }.toSingleDefault(Result.success())
                        }
                    }
                }.onErrorResumeNext {
                    Single.just(Result.retry())
                }
            }
        }
    }

    private fun getExposureConfiguration(): Single<ExposureConfigurationItem> {
        return remoteConfigurationRepository.update()
            .andThen(remoteConfigurationRepository.getExposureConfigurationItem())
    }

    private fun finalizeDiagnosisKeyProviding(diagnosisKeyFiles: List<File>) {
        Timber.d("finalizeDiagnosisKeyProviding")
        val analysedFilesTimestamps = mutableListOf<Long>()

        diagnosisKeyFiles
            .forEach { file ->
                DiagnosisKeysFileNameToTimestampUseCase().execute(file.name)?.let {
                    analysedFilesTimestamps.add(it)
                }
                file.delete()
            }

        analysedFilesTimestamps.max()?.let { largestAnalysedFilesTimestamp ->
            diagnosisKeyRepository.setLatestProcessedDiagnosisKeyTimestamp(
                largestAnalysedFilesTimestamp
            )
        }
    }

    override fun getBackgroundScheduler(): Scheduler {
        return Schedulers.io()
    }
}
