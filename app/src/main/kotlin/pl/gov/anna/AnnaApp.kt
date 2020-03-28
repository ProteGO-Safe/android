package pl.gov.anna

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import pl.gov.anna.di.*
import timber.log.Timber

class AnnaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initKoin() = startKoin {
        androidContext(this@AnnaApp)
        modules(
            listOf(
                gcsModule,
                viewModule,
                filesModule,
                appModule,
                domainModule,
                networkingModule
            )
        )
    }
}