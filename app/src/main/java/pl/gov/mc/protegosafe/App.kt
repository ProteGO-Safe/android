package pl.gov.mc.protegosafe

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.facebook.stetho.Stetho
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.jakewharton.threetenabp.AndroidThreeTen
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.realm.Realm
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.get
import org.koin.core.inject
import org.threeten.bp.LocalDateTime
import org.threeten.bp.OffsetDateTime
import pl.gov.mc.protegosafe.data.BuildConfig
import pl.gov.mc.protegosafe.data.db.realm.RealmDatabaseBuilder
import pl.gov.mc.protegosafe.data.di.dataModule
import pl.gov.mc.protegosafe.data.extension.copyTo
import pl.gov.mc.protegosafe.di.appModule
import pl.gov.mc.protegosafe.di.deviceModule
import pl.gov.mc.protegosafe.di.useCaseModule
import pl.gov.mc.protegosafe.di.viewModelModule
import pl.gov.mc.protegosafe.domain.repository.CertificatePinningRepository
import pl.gov.mc.protegosafe.domain.repository.DiagnosisKeyRepository
import pl.gov.mc.protegosafe.domain.scheduler.ApplicationTaskScheduler
import pl.gov.mc.protegosafe.domain.usecase.PrepareMigrationIfRequiredUseCase
import timber.log.Timber

class App : Application(), KoinComponent {

    private val disposables = CompositeDisposable()
    private val applicationTaskScheduler: ApplicationTaskScheduler by inject()
    private val certificatePinningRepository: CertificatePinningRepository by inject()

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(appModule, deviceModule, useCaseModule, dataModule, viewModelModule)
        }

        prepareMigrationIfRequired()
        initializePinning()
        initializeDatabase()
        initializeLogging()
        initializeFcm()
        initializeStetho()
        initializeThreeTenABP()
        scheduleRemoveOldExposuresTask()
        setTemporaryExposureKeysDownloadTimestampIfEmpty()
    }

    private fun prepareMigrationIfRequired() {
        get<PrepareMigrationIfRequiredUseCase>().execute(
            pl.gov.mc.protegosafe.BuildConfig.VERSION_NAME
        ).subscribe(
            {
                removeAllOpenTraceData()
                encryptSharedPrefsIfNeeded()
            },
            {
                Timber.e(it, "PrepareMigrationIfRequiredUseCase: failed")
            }
        ).addTo(disposables)
    }

    private fun initializePinning() {
        certificatePinningRepository.initialize()
    }

    private fun initializeThreeTenABP() {
        AndroidThreeTen.init(this)
    }

    private fun scheduleRemoveOldExposuresTask() {
        applicationTaskScheduler.scheduleRemoveOldExposuresTask()
    }

    private fun initializeDatabase() {
        Realm.init(this)
        val realmDatabaseBuilder: RealmDatabaseBuilder by inject()
        Realm.setDefaultConfiguration(realmDatabaseBuilder.build())
    }

    private fun encryptSharedPrefsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // TODO PSAFE-1074
            val regularSharedPrefsFileName = "shared_prefs"
            val regularSharedPreferences = getSharedPreferences(
                regularSharedPrefsFileName, Context.MODE_PRIVATE
            )
            val encryptedSharedPreferences = get<SharedPreferences>()
            regularSharedPreferences.copyTo(encryptedSharedPreferences)
            regularSharedPreferences.edit().clear().apply()
        }
    }

    private fun removeAllOpenTraceData() {
        val dbName = "record_database"
        val sharedPrefsName = "Tracer_pref"
        deleteDatabase(dbName)
        getSharedPreferences(sharedPrefsName, Context.MODE_PRIVATE)
            .edit().clear().apply()
    }

    override fun onTerminate() {
        disposables.clear()
        super.onTerminate()
    }

    private fun initializeLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun setTemporaryExposureKeysDownloadTimestampIfEmpty() {
        get<DiagnosisKeyRepository>().apply {
            if (getLatestProcessedDiagnosisKeyTimestamp() == 0L) {
                setLatestProcessedDiagnosisKeyTimestamp(
                    LocalDateTime.now().toInstant(OffsetDateTime.now().offset).epochSecond
                )
            }
        }
    }

    private fun initializeFcm() {
        FirebaseApp.initializeApp(this)
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(
                OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Timber.w(task.exception, "Couldn't get FCM token")
                        return@OnCompleteListener
                    }
                }
            )

        BuildConfig.MAIN_TOPIC.let {
            FirebaseMessaging.getInstance().subscribeToTopic(it)
                .addOnCompleteListener { task ->
                    Timber.d(
                        if (task.isSuccessful) "FCM MAIN topic subscribe success - $it"
                        else "FCM MAIN topic subscribe failed - $it"
                    )
                }
        }

        BuildConfig.DAILY_TOPIC.let {
            FirebaseMessaging.getInstance().subscribeToTopic(it)
                .addOnCompleteListener { task ->
                    Timber.d(
                        if (task.isSuccessful) "FCM DAILY topic subscribe success - $it"
                        else "FCM DAILY topic subscribe failed - $it"
                    )
                }
        }
    }

    private fun initializeStetho() {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }
}
