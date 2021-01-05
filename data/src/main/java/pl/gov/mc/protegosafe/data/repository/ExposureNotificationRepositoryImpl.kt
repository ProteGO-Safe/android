package pl.gov.mc.protegosafe.data.repository

import com.google.android.gms.nearby.exposurenotification.ExposureConfiguration
import com.google.android.gms.nearby.exposurenotification.ExposureNotificationClient
import io.reactivex.Completable
import io.reactivex.Single
import java.io.File
import java.security.SecureRandom
import pl.gov.mc.protegosafe.data.extension.toBase64
import pl.gov.mc.protegosafe.data.extension.toCompletable
import pl.gov.mc.protegosafe.data.extension.toSingle
import pl.gov.mc.protegosafe.data.mapper.toEntity
import pl.gov.mc.protegosafe.data.mapper.toExposureConfiguration
import pl.gov.mc.protegosafe.domain.model.ApiExceptionMapper
import pl.gov.mc.protegosafe.domain.model.ExposureConfigurationItem
import pl.gov.mc.protegosafe.domain.model.ExposureInformationItem
import pl.gov.mc.protegosafe.domain.model.ExposureNotificationStatusItem
import pl.gov.mc.protegosafe.domain.model.ExposureSummaryItem
import pl.gov.mc.protegosafe.domain.model.ResolutionRequest
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeyItem
import pl.gov.mc.protegosafe.domain.repository.ExposureNotificationRepository
import timber.log.Timber

class ExposureNotificationRepositoryImpl(
    private val exposureNotificationClient: ExposureNotificationClient,
    private val apiExceptionMapper: ApiExceptionMapper
) : ExposureNotificationRepository {

    override val ACTION_EXPOSURE_STATE_UPDATED: String
        get() = ExposureNotificationClient.ACTION_EXPOSURE_STATE_UPDATED
    override val ACTION_EXPOSURE_NOT_FOUND: String
        get() = ExposureNotificationClient.ACTION_EXPOSURE_NOT_FOUND
    override val EXTRA_TOKEN: String
        get() = ExposureNotificationClient.EXTRA_TOKEN

    override fun start(): Completable {
        Timber.d("Exposure Notification Start")
        return exposureNotificationClient.start().toCompletable()
            .onErrorResumeNext {
                Completable.error(it.toNotResolvedException(ResolutionRequest.START_EXPOSURE_NOTIFICATION))
            }
    }

    override fun stop(): Completable {
        Timber.d("Exposure Notification Stop")
        return exposureNotificationClient.stop().toCompletable()
    }

    override fun isEnabled(): Single<Boolean> {
        Timber.d("isEnabled")
        return exposureNotificationClient.isEnabled.toSingle()
    }

    override fun getTemporaryExposureKeyHistory(): Single<List<TemporaryExposureKeyItem>> {
        Timber.d("getTemporaryExposureKeyHistory")
        return exposureNotificationClient.temporaryExposureKeyHistory
            .toSingle()
            .onErrorResumeNext {
                Single.error(it.toNotResolvedException(ResolutionRequest.ACCESS_TEMPORARY_EXPOSURE_KEYS))
            }
            .map { list -> list.map { it.toEntity() } }
    }

    override fun provideDiagnosisKeys(
        files: List<File>,
        token: String,
        exposureConfigurationItem: ExposureConfigurationItem?
    ): Completable {
        Timber.d("provideDiagnosisKeys")
        return exposureNotificationClient.provideDiagnosisKeys(
            files,
            exposureConfigurationItem?.toExposureConfiguration()
                ?: ExposureConfiguration.ExposureConfigurationBuilder().build(),
            token
        ).toCompletable()
    }

    override fun getExposureSummary(token: String): Single<ExposureSummaryItem> {
        Timber.d("getExposureSummary")
        return exposureNotificationClient.getExposureSummary(token).toSingle()
            .map { it.toEntity() }
    }

    override fun getExposureInformation(token: String): Single<List<ExposureInformationItem>> {
        Timber.d("getExposureInformation")
        return exposureNotificationClient.getExposureInformation(token).toSingle()
            .map { list -> list.map { it.toEntity() } }
    }

    override fun generateRandomToken(): String = ByteArray(RANDOM_TOKEN_BYTE_LENGTH)
        .apply { SecureRandom().nextBytes(this) }
        .toBase64()
        .also {
            Timber.d("generateRandomToken = $it")
        }

    override fun getExposureNotificationState(): Single<ExposureNotificationStatusItem> {
        Timber.d("getExposureNotificationState")
        return start()
            .toSingleDefault(ExposureNotificationStatusItem.ON)
            .onErrorReturn { apiExceptionMapper.toStatus(it) }
    }

    private fun Throwable.toNotResolvedException(resolutionRequest: ResolutionRequest): Throwable =
        apiExceptionMapper.toExposureNotificationActionNotResolvedException(this, resolutionRequest)
            ?: this
}

private const val RANDOM_TOKEN_BYTE_LENGTH = 32
