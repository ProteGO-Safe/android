package pl.gov.anna.di

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel

import org.koin.core.module.Module
import org.koin.dsl.module
import pl.gov.anna.BuildConfig
import pl.gov.anna.backend.api.RegistrationAPI
import pl.gov.anna.backend.api.RegistrationRequestComposer
import pl.gov.anna.backend.api.RegistrationService
import pl.gov.anna.backend.api.RequestComposer
import pl.gov.anna.backend.domain.AnnaServer
import pl.gov.anna.file.FileManager
import pl.gov.anna.information.AppInformation
import pl.gov.anna.information.PhoneInformation
import pl.gov.anna.information.Session
import pl.gov.anna.ui.registration.RegistrationConfirmationViewModel
import pl.gov.anna.ui.registration.RegistrationViewModel
import pl.gov.anna.ui.validator.MsisdnValidator
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

val viewModule: Module = module {
    viewModel { RegistrationViewModel(get(), get()) }
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
}

val domainModule = module {
    single { AnnaServer(get(), get()) }
    single { RegistrationService(get(), get(), get()) }
    single { RegistrationRequestComposer(get()) }
    single { RequestComposer(get(), get(), get()) }
    single { Session() }
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