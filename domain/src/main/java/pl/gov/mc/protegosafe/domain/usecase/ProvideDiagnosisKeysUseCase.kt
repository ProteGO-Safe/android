package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.ExposureConfigurationItem
import pl.gov.mc.protegosafe.domain.repository.DiagnosisKeyRepository
import pl.gov.mc.protegosafe.domain.repository.ExposureNotificationRepository
import java.io.File

class ProvideDiagnosisKeysUseCase(
    private val exposureNotificationRepository: ExposureNotificationRepository,
    private val diagnosisKeyRepository: DiagnosisKeyRepository,
    private val postExecutionThread: PostExecutionThread
) {
    /**
     * @param files - List of files that contain key information
     * @param token - A unique token for this batch can also be provided,
     * which will be used to associate the matches with
     * this request as part of {@link #getExposureSummary} and
     * {@link #getExposureInformation}.
     * @param exposureConfigurationItem - Exposure configuration
     * @result will be provided with #ACTION_EXPOSURE_STATE_UPDATED broadcast
     */
    fun execute(
        files: List<File>,
        token: String,
        exposureConfigurationItem: ExposureConfigurationItem? = null
    ): Completable {
        return exposureNotificationRepository.provideDiagnosisKeys(
            files,
            token,
            exposureConfigurationItem
        ).andThen(
            finalizeDiagnosisKeyProviding(files)
        )
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun finalizeDiagnosisKeyProviding(files: List<File>): Completable {
        return Maybe.fromCallable {
            files.map {
                DiagnosisKeysFileNameToTimestampUseCase().execute(
                    it.name
                )
            }.sortedBy { it }
        }.flatMapCompletable { sortedFiles ->
            sortedFiles.lastOrNull()?.let {
                diagnosisKeyRepository.setLatestProcessedDiagnosisKeyTimestamp(it)
            } ?: Completable.complete()
                .andThen(
                    Completable.fromAction {
                        files.forEach { it.delete() }
                    }
                )
        }
    }
}
