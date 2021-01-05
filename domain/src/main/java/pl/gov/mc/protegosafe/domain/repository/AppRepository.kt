package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import java.util.Locale

interface AppRepository {
    fun getVersionName(): Single<String>

    /**
     * @return String in ISO 639-1 standard
     */
    fun getSystemLanguage(): Single<String>

    /**
     * @return String in ISO 639-1 standard
     */
    fun getAppLanguage(): Single<String>

    fun setAppLanguage(languageISO: String): Completable

    fun getLocale(): Locale

    fun getFontScale(): Single<Float>

    /**
     * Delete Realm database and clear Shared Preferences
     */
    fun clearAppData(): Completable

    fun createRealmDatabase(): Completable

    fun getWorkersIntervalInMinutes(): Long

    fun setWorkersInterval(intervalInMinutes: Long)

    fun setCovidStatsNotificationsAgreement(isAllowed: Boolean): Completable

    fun areCovidStatsNotificationsAllowed(): Single<Boolean>

    fun subscribeToCovidStatsNotificationsTopic(): Completable

    fun unsubscribeFromCovidStatsNotificationsTopic(): Completable
}
