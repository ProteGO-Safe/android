package pl.gov.mc.protegosafe.data

import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import pl.gov.mc.protegosafe.domain.OpenTraceWrapper

val deviceModule = module {
    single<OpenTraceWrapper> { OpenTraceWrapperImpl(androidApplication()) }
    //TODO: remove, we should inject it only by interface
    single { OpenTraceWrapperImpl(androidApplication()) }

}