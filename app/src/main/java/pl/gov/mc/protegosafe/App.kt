package pl.gov.mc.protegosafe

import com.facebook.stetho.Stetho
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import io.bluetrace.opentrace.TracerApp
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import pl.gov.mc.protegosafe.data.BuildConfig
import pl.gov.mc.protegosafe.data.di.dataModule
import pl.gov.mc.protegosafe.di.appModule
import pl.gov.mc.protegosafe.di.deviceModule
import pl.gov.mc.protegosafe.di.useCaseModule
import pl.gov.mc.protegosafe.di.viewModelModule
import pl.gov.mc.protegosafe.domain.DumpTraceDataUseCase
import timber.log.Timber

class App : TracerApp() {

    private val disposables = CompositeDisposable()

    private val dumpTraceDataUseCase by inject<DumpTraceDataUseCase>()

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        startKoin {
            androidContext(this@App)
            modules(appModule, deviceModule, useCaseModule, dataModule, viewModelModule)
        }

        initializeFcm()
        initializeStetho()
        if (BuildConfig.DEBUG) {
            initializeHyperionDebugMenu(dumpTraceDataUseCase)
        }
    }

    override fun onTerminate() {
        disposables.clear()
        super.onTerminate()
    }

    private fun initializeFcm() {
        FirebaseApp.initializeApp(this)
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Timber.w(task.exception, "Couldn't get FCM token")
                    return@OnCompleteListener
                }

                val token = task.result?.token
                // Log and toast
                Timber.d("FCM token $token")
            })

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
