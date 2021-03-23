package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable

interface MigrationRepository {
    enum class LastAppVersion(val url: String) {
        VERSION_3(VERSION_3_URL),
        VERSION_4_1(VERSION_4_1_URL),
        MIGRATION_NOT_REQUIRED("")
    }

    fun updateCurrentVersion(versionName: String)
    fun getMigrationUrlAndClear(): String
    fun prepareMigrationUrlIfRequired(): Completable
    fun prepareCovidInfoDataStoreMigrationIfRequired(): Completable
}
private const val VERSION_3_URL = "https://safesafe.app"
private const val VERSION_4_1_URL = "https://v4.safesafe.app/"

enum class CovidInfoDataStoreMigration(val oldVersion: Long, val newVersion: Long) {
    VERSION_1_2(1, 2);
}
