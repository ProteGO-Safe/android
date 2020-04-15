package se.sigmaconnectivity.blescanner

import android.app.Application
import com.facebook.stetho.Stetho
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import se.sigmaconnectivity.blescanner.data.dataModule
import se.sigmaconnectivity.blescanner.di.appModule
import se.sigmaconnectivity.blescanner.di.viewModelModule
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
    }
}