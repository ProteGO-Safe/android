package pl.gov.mc.protego

import android.app.Application
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import pl.gov.mc.protego.bluetooth.BluetoothBeaconIdExchangeManager
import pl.gov.mc.protego.di.*
import timber.log.Timber


class ProtegoApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        getKoin().get<BluetoothBeaconIdExchangeManager>().start()
    }

    private fun initKoin() = startKoin {
        androidContext(this@ProtegoApp)
        modules(
            listOf(
                gcsModule,
                viewModule,
                filesModule,
                appModule,
                domainModule,
                networkingModule,
                bluetoothModule
            )
        )
    }
}