package pl.gov.mc.protegosafe.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import pl.gov.mc.protegosafe.OpenTraceWrapper
import pl.gov.mc.protegosafe.domain.OpenTraceRepository

val deviceModule = module {
    single<OpenTraceRepository> { OpenTraceWrapper(androidContext()) }
}