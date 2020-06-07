package pl.gov.mc.protegosafe.data.repository

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import io.reactivex.Completable
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import pl.gov.mc.protegosafe.data.R
import pl.gov.mc.protegosafe.data.extension.toCompletable
import pl.gov.mc.protegosafe.domain.model.DiagnosisKeyDownloadConfiguration
import pl.gov.mc.protegosafe.domain.model.ExposureConfigurationItem
import pl.gov.mc.protegosafe.domain.model.ExposureConfigurationMapper
import pl.gov.mc.protegosafe.domain.model.DiagnosisKeyDownloadConfigurationMapper
import pl.gov.mc.protegosafe.domain.repository.RemoteConfigurationRepository
import timber.log.Timber

class RemoteConfigurationRepositoryImpl(
    private val exposureConfigurationMapper: ExposureConfigurationMapper,
    private val diagnosisKeyDownloadConfigurationMapper: DiagnosisKeyDownloadConfigurationMapper
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
                _remoteConfig.getString("exposureConfiguration").let { configurationJson ->
                    exposureConfigurationMapper.toEntity(configurationJson)
                }
            })
    }

    override fun getDiagnosisKeyDownloadConfiguration(): Single<DiagnosisKeyDownloadConfiguration> {
        Timber.d("getDiagnosisKeyDownloadConfiguration")
        return setUpConfigSettingsIfNeeded()
            .andThen(Single.fromCallable {
                _remoteConfig.getString("diagnosisKeyDownloadConfiguration")
                    .let { configurationJson ->
                        diagnosisKeyDownloadConfigurationMapper.toEntity(configurationJson)
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
}
