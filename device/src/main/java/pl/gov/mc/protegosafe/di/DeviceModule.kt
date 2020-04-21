package pl.gov.mc.protegosafe.di

import com.google.firebase.functions.FirebaseFunctions
import io.bluetrace.opentrace.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import pl.gov.mc.protegosafe.OpenTraceWrapper
import pl.gov.mc.protegosafe.domain.repository.OpenTraceRepository

val deviceModule = module {
    single<OpenTraceRepository> { OpenTraceWrapper(androidContext(), get()) }
    single { FirebaseFunctions.getInstance(BuildConfig.FIREBASE_REGION) }
}