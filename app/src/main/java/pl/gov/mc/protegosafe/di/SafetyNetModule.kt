package pl.gov.mc.protegosafe.di

import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.safetynet.SafetyNet
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import pl.gov.mc.protegosafe.manager.SafetyNetManager
import pl.gov.mc.protegosafe.mapper.safetynet.SafetyNetMapper

val safetyNetModule = module {
    single { SafetyNetMapper() }
    factory { SafetyNet.getClient(androidContext()) }
    factory { GoogleApiAvailability.getInstance() }
    single { SafetyNetManager(androidContext(), get(), get(), get()) }
}