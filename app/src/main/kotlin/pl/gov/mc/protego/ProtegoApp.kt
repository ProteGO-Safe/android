package pl.gov.mc.protego

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import pl.gov.mc.protego.di.*
import timber.log.Timber

class ProtegoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initKoin() = startKoin {
        androidContext(this@ProtegoApp)
        modules(
            variantSpecificModulesList
                .plus(
                    listOf(
                        gcsModule,
                        viewModule,
                        filesModule,
                        appModule,
                        domainModule,
                        networkingModule,
                        securityModule,
                        utilModule
                    )
                )
        )
    }
}