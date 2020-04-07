package pl.gov.mc.protego.di

import android.bluetooth.BluetoothManager
import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel

import org.koin.core.module.Module
import org.koin.dsl.module
import pl.gov.mc.protego.BuildConfig
import pl.gov.mc.protego.backend.api.RegistrationAPI
import pl.gov.mc.protego.backend.api.RegistrationRequestComposer
import pl.gov.mc.protego.backend.api.RegistrationService
import pl.gov.mc.protego.backend.api.RequestComposer
import pl.gov.mc.protego.backend.domain.ProtegoServer
import pl.gov.mc.protego.bluetooth.BeaconIdManager
import pl.gov.mc.protego.bluetooth.BluetoothBeaconIdExchangeManager
import pl.gov.mc.protego.bluetooth.advertiser.AdvertiserInterface
import pl.gov.mc.protego.bluetooth.advertiser.ProteGoAdvertiser
import pl.gov.mc.protego.bluetooth.beacon.BeaconIdAgent
import pl.gov.mc.protego.bluetooth.scanner.ProteGoScanner
import pl.gov.mc.protego.bluetooth.scanner.ScannerInterface
import pl.gov.mc.protego.file.FileManager
import pl.gov.mc.protego.information.AppInformation
import pl.gov.mc.protego.information.PhoneInformation
import pl.gov.mc.protego.information.Session
import pl.gov.mc.protego.repository.SessionRepository
import pl.gov.mc.protego.ui.registration.RegistrationConfirmationViewModel
import pl.gov.mc.protego.ui.registration.RegistrationViewModel
import pl.gov.mc.protego.ui.validator.MsisdnValidator
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

val viewModule: Module = module {
    viewModel { RegistrationViewModel(get(), get(), get()) }
    viewModel { RegistrationConfirmationViewModel(get()) }
    single { MsisdnValidator() }
}

val gcsModule: Module = module {
}

val filesModule: Module = module {
    single { FileManager(get()) }
}

val appModule = module {
    single { PhoneInformation() }
    single { AppInformation() }
    single{
        androidApplication().getSharedPreferences("ProteGo",  Context.MODE_PRIVATE)
    }
}

val domainModule = module {
    single { ProtegoServer(get(), get()) }
    single { RegistrationService(get(), get(), get()) }
    single { RegistrationRequestComposer(get()) }
    single { RequestComposer(get(), get(), get()) }
    single { Session(get()) }
    single { SessionRepository(get()) }
}

val networkingModule = module {

    single {
        HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Timber.tag("OkHttp").d(message)
            }
        }).setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    single {

        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_API_URL)
            .client(get())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        get<Retrofit>().create(RegistrationAPI::class.java)
    }
}

val bluetoothModule = module {
    single<BeaconIdAgent> { BeaconIdManager() }
    single { androidApplication().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager }
    single<AdvertiserInterface> { ProteGoAdvertiser(androidApplication(), get(), get()) }
    single<ScannerInterface> { ProteGoScanner(androidApplication(), get()) }
    single { BluetoothBeaconIdExchangeManager(get(), get()) }
}