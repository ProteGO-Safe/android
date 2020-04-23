package pl.gov.mc.protegosafe.di

import com.google.firebase.functions.FirebaseFunctions
import io.bluetrace.opentrace.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import pl.gov.mc.protegosafe.DeviceRepositoryImpl
import pl.gov.mc.protegosafe.OpenTraceWrapper
import pl.gov.mc.protegosafe.domain.model.TraceStatusMapper
import pl.gov.mc.protegosafe.domain.manager.InternetConnectionManager
import pl.gov.mc.protegosafe.domain.repository.DeviceRepository
import pl.gov.mc.protegosafe.domain.repository.OpenTraceRepository
import pl.gov.mc.protegosafe.mapper.TraceStatusMapperImpl
import pl.gov.mc.protegosafe.manager.InternetConnectionManagerImpl

val deviceModule = module {
    single<OpenTraceRepository> { OpenTraceWrapper(androidContext(), get()) }
    single { FirebaseFunctions.getInstance(BuildConfig.FIREBASE_REGION) }
    single<InternetConnectionManager> {
        InternetConnectionManagerImpl(
            context = androidContext()
        )
    }
    single<DeviceRepository> { DeviceRepositoryImpl(androidContext(), get()) }

    factory<TraceStatusMapper> { TraceStatusMapperImpl() }
}