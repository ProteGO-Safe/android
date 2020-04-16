package pl.gov.mc.protegosafe

import android.app.Application
import com.facebook.stetho.Stetho
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import pl.gov.mc.protegosafe.data.dataModule
import pl.gov.mc.protegosafe.di.appModule
import pl.gov.mc.protegosafe.di.viewModelModule
import timber.log.Timber

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        startKoin {
            androidContext(this@App)
            modules(appModule, dataModule, viewModelModule)
        }

        Stetho.initializeWithDefaults(this)
        initializeFcm()
    }

    private fun initializeFcm() {
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

        FirebaseMessaging.getInstance().subscribeToTopic("general")
            .addOnCompleteListener { task ->
                Timber.d(
                    if (!task.isSuccessful) "FCM GENERAL topic subscribe success"
                    else "FCM GENERAL topic subscribe failed"
                )
            }

        FirebaseMessaging.getInstance().subscribeToTopic("daily")
            .addOnCompleteListener { task ->
                Timber.d(
                    if (!task.isSuccessful) "FCM DAILY topic subscribe success"
                    else "FCM DAILY topic subscribe failed"
                )
            }
    }
}