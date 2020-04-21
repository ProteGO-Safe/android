package pl.gov.mc.protegosafe.di

import com.google.firebase.functions.FirebaseFunctions
import io.bluetrace.opentrace.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import pl.gov.mc.protegosafe.OpenTraceWrapper
import pl.gov.mc.protegosafe.domain.repository.OpenTraceRepository
import pl.gov.mc.protegosafe.domain.usecase.IGetInternetConnectionStatusUseCase
import pl.gov.mc.protegosafe.usecase.GetInternetConnectionStatusUseCase

val deviceModule = module {
    single<OpenTraceRepository> { OpenTraceWrapper(androidContext(), get()) }
    single { FirebaseFunctions.getInstance(BuildConfig.FIREBASE_REGION) }
    factory<IGetInternetConnectionStatusUseCase> {
        GetInternetConnectionStatusUseCase(
            context = androidContext()
        )
    }
}