package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.model.DiagnosisKeyDownloadConfiguration
import pl.gov.mc.protegosafe.domain.model.ExposureConfigurationItem

interface RemoteConfigurationRepository {

    /**
     * Update Remote Configuration Repository from the backend.
     */
    fun update(): Completable

    /**
     * Get [ExposureConfigurationItem] stored in remote configuration.
     */
    fun getExposureConfigurationItem(): Single<ExposureConfigurationItem>

    /**
     * Get [DiagnosisKeyDownloadConfiguration] stored in remote configuration.
     */
    fun getDiagnosisKeyDownloadConfiguration(): Single<DiagnosisKeyDownloadConfiguration>
}
