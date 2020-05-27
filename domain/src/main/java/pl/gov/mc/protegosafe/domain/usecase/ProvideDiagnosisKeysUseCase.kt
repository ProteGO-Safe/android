package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import java.io.File
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.model.ExposureConfigurationItem
import pl.gov.mc.protegosafe.domain.model.ExposureConfigurationMapper
import pl.gov.mc.protegosafe.domain.repository.ExposureNotificationRepository

class ProvideDiagnosisKeysUseCase(
    private val exposureNotificationRepository: ExposureNotificationRepository,
    private val exposureConfigurationMapper: ExposureConfigurationMapper,
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
        token: String = exposureNotificationRepository.generateRandomToken(),
        exposureConfigurationItem: ExposureConfigurationItem? = null
    ): Completable {
        return exposureNotificationRepository.provideDiagnosisKeys(
            files,
            token,
            exposureConfigurationItem
        )
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}
