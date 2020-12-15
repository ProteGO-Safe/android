package pl.gov.mc.protegosafe

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.Completable
import io.reactivex.Single
import io.realm.Realm
import pl.gov.mc.protegosafe.data.db.AppLanguageDataStore
import pl.gov.mc.protegosafe.data.db.CovidStatsDataStore
import pl.gov.mc.protegosafe.data.db.WorkersIntervalDataStore
import pl.gov.mc.protegosafe.data.db.realm.RealmDatabaseBuilder
import pl.gov.mc.protegosafe.data.extension.toCompletable
import pl.gov.mc.protegosafe.domain.repository.AppRepository
import timber.log.Timber
import java.util.Locale

class AppRepositoryImpl(
    private val appLanguageDataStore: AppLanguageDataStore,
    private val sharedPreferences: SharedPreferences,
    private val realmDatabaseBuilder: RealmDatabaseBuilder,
    private val covidStatsDataStore: CovidStatsDataStore,
    private val workersIntervalDataStore: WorkersIntervalDataStore,
    private val context: Context
) : AppRepository {
    override fun getVersionName(): Single<String> {
        return Single.just(BuildConfig.VERSION_NAME)
    }

    override fun getSystemLanguage(): Single<String> {
        return Single.just(Locale.getDefault().language.toUpperCase(Locale.getDefault()))
    }

    override fun getLocale(): Locale {
        return Locale(appLanguageDataStore.appLanguageISO)
    }

    override fun setAppLanguage(languageISO: String): Completable {
        return Completable.fromAction {
            appLanguageDataStore.appLanguageISO = languageISO
        }
    }

    override fun getAppLanguage(): Single<String> {
        return Single.fromCallable {
            appLanguageDataStore.appLanguageISO
        }
    }

    override fun getFontScale(): Single<Float> {
        return Single.just(context.resources.configuration.fontScale)
    }

    override fun clearAppData(): Completable {
        return clearDatabase()
            .andThen(
                clearSharedPreferences()
            )
    }

    private fun clearDatabase(): Completable {
        return Completable.fromAction {
            Realm.deleteRealm(realmDatabaseBuilder.build())
        }
    }

    @SuppressLint("ApplySharedPref")
    private fun clearSharedPreferences(): Completable {
        return Completable.fromAction {
            sharedPreferences.edit().clear().commit()
        }
    }

    override fun createRealmDatabase(): Completable {
        return Completable.fromAction {
            Realm.init(context)
            Realm.setDefaultConfiguration(realmDatabaseBuilder.build())
        }
    }

    override fun getWorkersIntervalInMinutes(): Long {
        return workersIntervalDataStore.timeIntervalInMinutes
    }

    override fun setWorkersInterval(intervalInMinutes: Long) {
        workersIntervalDataStore.timeIntervalInMinutes = intervalInMinutes
    }

    override fun setCovidStatsNotificationsAgreement(isAllowed: Boolean): Completable {
        return Completable.fromAction {
            covidStatsDataStore.covidStatsNotificationsAgreement = isAllowed
        }
    }

    override fun areCovidStatsNotificationsAllowed(): Single<Boolean> {
        return Single.fromCallable {
            covidStatsDataStore.covidStatsNotificationsAgreement
        }
    }

    override fun subscribeToCovidStatsNotificationsTopic(): Completable {
        return pl.gov.mc.protegosafe.data.BuildConfig.COVID_STATS_TOPIC.let {
            Timber.d("Subscribed to: $it")
            FirebaseMessaging.getInstance().subscribeToTopic(it)
                .toCompletable()
        }
    }

    override fun unsubscribeFromCovidStatsNotificationsTopic(): Completable {
        return pl.gov.mc.protegosafe.data.BuildConfig.COVID_STATS_TOPIC.let {
            Timber.d("Unsubscribed from: $it")
            FirebaseMessaging.getInstance().unsubscribeFromTopic(it)
                .toCompletable()
        }
    }
}
