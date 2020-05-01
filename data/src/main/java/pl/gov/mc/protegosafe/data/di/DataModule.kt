package pl.gov.mc.protegosafe.data.di

import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import pl.gov.mc.protegosafe.data.AuthRepositoryImpl
import pl.gov.mc.protegosafe.data.NotificationRepositoryImpl
import pl.gov.mc.protegosafe.data.TrackingRepositoryImpl
import pl.gov.mc.protegosafe.data.TriageRepositoryImpl
import pl.gov.mc.protegosafe.data.db.NotificationDataStore
import pl.gov.mc.protegosafe.data.db.SharedPreferencesDelegates
import pl.gov.mc.protegosafe.data.db.TrackingDataStore
import pl.gov.mc.protegosafe.data.db.TriageDataStore
import pl.gov.mc.protegosafe.data.mapper.ClearMapperImpl
import pl.gov.mc.protegosafe.domain.model.ClearMapper
import pl.gov.mc.protegosafe.domain.repository.AuthRepository
import pl.gov.mc.protegosafe.domain.repository.NotificationRepository
import pl.gov.mc.protegosafe.domain.repository.TrackingRepository
import pl.gov.mc.protegosafe.domain.repository.TriageRepository

val dataModule = module {
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
    single<TriageRepository> { TriageRepositoryImpl(get()) }
    single<TrackingRepository> { TrackingRepositoryImpl(get()) }
    single { NotificationDataStore() }
    single { TriageDataStore(get()) }
    single { TrackingDataStore(get()) }
    single { SharedPreferencesDelegates(androidApplication()) }
    single { FirebaseAuth.getInstance() }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    factory<ClearMapper> { ClearMapperImpl() }

}