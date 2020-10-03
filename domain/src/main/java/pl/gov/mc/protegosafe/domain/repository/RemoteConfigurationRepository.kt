package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import pl.gov.mc.protegosafe.domain.model.DiagnosisKeyDownloadConfiguration
import pl.gov.mc.protegosafe.domain.model.ExposureConfigurationItem
import pl.gov.mc.protegosafe.domain.model.RiskLevelConfigurationItem

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

    /**
     * Get [RiskLevelConfigurationItem] stored in remote configuration.
     */
    fun getRiskLevelConfiguration(): Single<RiskLevelConfigurationItem>
}
