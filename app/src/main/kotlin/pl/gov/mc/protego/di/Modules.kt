package pl.gov.mc.protego.di

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import pl.gov.mc.protego.BuildConfig
import pl.gov.mc.protego.backend.api.*
import pl.gov.mc.protego.backend.domain.ProtegoServer
import pl.gov.mc.protego.encryption.EncryptionKeyStore
import pl.gov.mc.protego.encryption.RandomKey
import pl.gov.mc.protego.file.FileManager
import pl.gov.mc.protego.information.AppInformation
import pl.gov.mc.protego.information.PhoneInformation
import pl.gov.mc.protego.information.Session
import pl.gov.mc.protego.realm.RealmEncryption
import pl.gov.mc.protego.realm.RealmInitializer
import pl.gov.mc.protego.repository.SessionRepository
import pl.gov.mc.protego.ui.main.DashboardActivityViewModel
import pl.gov.mc.protego.ui.main.HistoryViewModel
import pl.gov.mc.protego.ui.registration.RegistrationConfirmationViewModel
import pl.gov.mc.protego.ui.registration.RegistrationViewModel
import pl.gov.mc.protego.ui.registration.onboarding.OnboardingViewModel
import pl.gov.mc.protego.ui.splash.SplashScreenViewModel
import pl.gov.mc.protego.ui.validator.MsisdnValidator
import pl.gov.mc.protego.util.EmailClientAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.security.SecureRandom

val viewModule: Module = module {
    viewModel { RegistrationViewModel(get(), get(), get()) }
    viewModel { RegistrationConfirmationViewModel(get()) }
    viewModel { DashboardActivityViewModel(get()) }
    viewModel { SplashScreenViewModel(get()) }
    viewModel { OnboardingViewModel() }
    viewModel { HistoryViewModel(get(), get(), get()) }
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
    single {
        androidApplication().getSharedPreferences("ProteGo", android.content.Context.MODE_PRIVATE)
    }
    single { androidContext().resources }
}

val utilModule = module {
    factory { EmailClientAdapter(get()) }
}

val domainModule = module {
    single { ProtegoServer(get(), get(), get()) }
    single { RegistrationService(get(), get(), get()) }
    single { StatusService(get(), get()) }
    single { RegistrationRequestComposer(get()) }
    single { StatusRequestComposer(get()) }
    single { RequestComposer(get(), get(), get()) }
    single { Session(get()) }
    single { SessionRepository(get()) }
}

val securityModule = module {
    single { RealmEncryption(get(), get()) }
    factory { RandomKey(get()) }
    factory { SecureRandom() }
    factory { EncryptionKeyStore() }
    single { RealmInitializer(get()) }
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

    single {
        get<Retrofit>().create(StatusApi::class.java)
    }
}