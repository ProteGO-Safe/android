package pl.gov.mc.protegosafe.data.repository

import android.content.Context
import android.content.SharedPreferences
import io.reactivex.Completable
import pl.gov.mc.protegosafe.data.db.AppVersionDataStore
import pl.gov.mc.protegosafe.data.db.CovidInfoDataStore
import pl.gov.mc.protegosafe.data.db.SHARED_PREFS_SAFETYNET_IS_DEVICE_CHECKED
import pl.gov.mc.protegosafe.domain.repository.CovidInfoDataStoreMigration
import pl.gov.mc.protegosafe.domain.repository.MigrationRepository

class MigrationRepositoryImpl(
    private val appVersionDataStore: AppVersionDataStore,
    private val context: Context,
    private val sharedPreferences: SharedPreferences,
    private val covidInfoDataStore: CovidInfoDataStore
) : MigrationRepository {

    override fun updateCurrentVersion(versionName: String) {
        appVersionDataStore.currentVersionName = versionName
    }

    override fun prepareMigrationUrlIfRequired(): Completable {
        return Completable.fromAction {
            val lastVersion = getLastVersion()
            if (lastVersion != MigrationRepository.LastAppVersion.MIGRATION_NOT_REQUIRED) {
                appVersionDataStore.lastVersionUrl = lastVersion.url
            }
        }
    }

    override fun getMigrationUrlAndClear(): String {
        return appVersionDataStore.lastVersionUrl.also {
            appVersionDataStore.lastVersionUrl = ""
        }
    }

    override fun prepareCovidInfoDataStoreMigrationIfRequired(): Completable {
        return Completable.fromAction {
            val lastMigration = CovidInfoDataStoreMigration.values().last()
            if (covidInfoDataStore.currentVersion != lastMigration.newVersion) {
                CovidInfoDataStoreMigration.values().forEach { migration ->
                    when (migration) {
                        CovidInfoDataStoreMigration.VERSION_1_2 -> {
                            covidInfoDataStore.voivodeshipsUpdateTimestamp = 0L
                        }
                    }
                    covidInfoDataStore.currentVersion = migration.newVersion
                }
            }
        }
    }

    private fun getLastVersion(): MigrationRepository.LastAppVersion {
        val version3SharedPrefs = context.getSharedPreferences(
            VERSION_3_SHARED_PREFS_NAME,
            Context.MODE_PRIVATE
        )
        return when {
            version3SharedPrefs.contains(VERSION_3_INDICATOR) -> {
                MigrationRepository.LastAppVersion.VERSION_3
            }
            sharedPreferences.contains(SHARED_PREFS_SAFETYNET_IS_DEVICE_CHECKED) &&
                appVersionDataStore.currentVersionName.isBlank() -> {
                MigrationRepository.LastAppVersion.VERSION_4_1
            }
            else -> {
                MigrationRepository.LastAppVersion.MIGRATION_NOT_REQUIRED
            }
        }
    }
}

private const val VERSION_3_SHARED_PREFS_NAME = "shared_prefs"
private const val VERSION_3_INDICATOR = "data.db.TrackingDataStore-tracking-agreement"
