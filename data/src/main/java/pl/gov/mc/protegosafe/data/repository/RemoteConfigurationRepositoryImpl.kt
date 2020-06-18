package pl.gov.mc.protegosafe.data.repository

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.data.R
import pl.gov.mc.protegosafe.data.extension.toCompletable
import pl.gov.mc.protegosafe.domain.model.DiagnosisKeyDownloadConfiguration
import pl.gov.mc.protegosafe.domain.model.DiagnosisKeyDownloadConfigurationMapper
import pl.gov.mc.protegosafe.domain.model.ExposureConfigurationItem
import pl.gov.mc.protegosafe.domain.model.ExposureConfigurationMapper
import pl.gov.mc.protegosafe.domain.model.RiskLevelConfigurationItem
import pl.gov.mc.protegosafe.domain.model.RiskLevelConfigurationMapper
import pl.gov.mc.protegosafe.domain.repository.RemoteConfigurationRepository
import timber.log.Timber
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

class RemoteConfigurationRepositoryImpl(
    private val exposureConfigurationMapper: ExposureConfigurationMapper,
    private val diagnosisKeyDownloadConfigurationMapper: DiagnosisKeyDownloadConfigurationMapper,
    private val riskLevelConfigurationMapper: RiskLevelConfigurationMapper
) : RemoteConfigurationRepository {

    private val _remoteConfig = Firebase.remoteConfig
    private var areDefaultSettingsSetUp = AtomicBoolean(false)

    override fun update(): Completable {
        Timber.d("update")
        return setUpConfigSettingsIfNeeded()
            .andThen(
                _remoteConfig.fetchAndActivate().toCompletable()
            )
    }

    override fun getExposureConfigurationItem(): Single<ExposureConfigurationItem> {
        Timber.d("getExposureConfigurationItem")
        return setUpConfigSettingsIfNeeded()
            .andThen(Single.fromCallable {
                _remoteConfig.getString(EXPOSURE_CONFIGURATION).let { configurationJson ->
                    exposureConfigurationMapper.toEntity(configurationJson)
                }
            })
    }

    override fun getDiagnosisKeyDownloadConfiguration(): Single<DiagnosisKeyDownloadConfiguration> {
        Timber.d("getDiagnosisKeyDownloadConfiguration")
        return setUpConfigSettingsIfNeeded()
            .andThen(Single.fromCallable {
                _remoteConfig.getString(DIAGNOSIS_KEY_DOWNLOAD_CONFIGURATION)
                    .let { configurationJson ->
                        diagnosisKeyDownloadConfigurationMapper.toEntity(configurationJson)
                    }
            })
    }

    override fun getRiskLevelConfiguration(): Single<RiskLevelConfigurationItem> {
        Timber.d("getExposureConfigurationItem")
        return setUpConfigSettingsIfNeeded()
            .andThen(Single.fromCallable {
                _remoteConfig.getString(RISK_LEVEL_CONFIGURATION).let { configurationJson ->
                    riskLevelConfigurationMapper.toEntity(configurationJson)
                }
            })
    }

    private fun setUpConfigSettingsIfNeeded() = Completable.defer {
        Timber.d("setUpConfigSettingsIfNeeded: isSetUpNeeded = ${!areDefaultSettingsSetUp.get()}")
        if (areDefaultSettingsSetUp.get()) {
            return@defer Completable.complete()
        }
        /*
         * 'minimumFetchIntervalInSeconds' stands that if configs in the local storage were
         * fetched more than this many seconds ago, configs are served from the backend instead
         * of local storage.
         */
        return@defer _remoteConfig.setConfigSettingsAsync(remoteConfigSettings {
            minimumFetchIntervalInSeconds = TimeUnit.HOURS.toSeconds(1)
        })
            .toCompletable()
            .andThen(_remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults).toCompletable())
            .doOnComplete { areDefaultSettingsSetUp.set(true) }
    }

    companion object {
        private const val EXPOSURE_CONFIGURATION = "exposureConfiguration"
        private const val DIAGNOSIS_KEY_DOWNLOAD_CONFIGURATION = "diagnosisKeyDownloadConfiguration"
        private const val RISK_LEVEL_CONFIGURATION = "riskLevelConfiguration"
    }
}
