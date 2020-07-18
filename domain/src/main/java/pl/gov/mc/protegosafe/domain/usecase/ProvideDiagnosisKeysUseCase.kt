package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
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

    companion object {
        private const val BATCH_DELIMITER = "-"
    }

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
        token: String = exposureNotificationRepository.generateRandomToken(),
        exposureConfigurationItem: ExposureConfigurationItem? = null
    ): Completable {
        return Completable.concat(
            filesToBatches(files)
                .sortedBy { it.first().name }
                .map { listOfFilesInBatch ->
                    exposureNotificationRepository.provideDiagnosisKeys(
                        listOfFilesInBatch,
                        token,
                        exposureConfigurationItem
                    ).doOnComplete {
                        finalizeDiagnosisKeyProviding(listOfFilesInBatch)
                    }
                }
        )
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun finalizeDiagnosisKeyProviding(listOfFilesInBatch: List<File>) {
        DiagnosisKeysFileNameToTimestampUseCase().execute(
            listOfFilesInBatch.first().name
        )?.let {
            diagnosisKeyRepository.setLatestProcessedDiagnosisKeyTimestamp(
                it
            )
        }
        listOfFilesInBatch.forEach { it.delete() }
    }

    private fun filesToBatches(files: List<File>) =
        files.groupBy {
            it.name.substringBefore(BATCH_DELIMITER)
        }.values.toList()
}
