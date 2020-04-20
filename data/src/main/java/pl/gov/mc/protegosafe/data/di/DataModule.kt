package pl.gov.mc.protegosafe.data.di

import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import pl.gov.mc.protegosafe.data.BuildConfig
import pl.gov.mc.protegosafe.data.NotificationRepositoryImpl
import pl.gov.mc.protegosafe.data.TriageRepositoryImpl
import pl.gov.mc.protegosafe.data.UserRepositoryImpl
import pl.gov.mc.protegosafe.data.db.NotificationDataStore
import pl.gov.mc.protegosafe.data.db.SharedPreferencesDelegates
import pl.gov.mc.protegosafe.data.db.TriageDataStore
import pl.gov.mc.protegosafe.data.db.UserIdStore
import pl.gov.mc.protegosafe.domain.NotificationRepository
import pl.gov.mc.protegosafe.domain.TriageRepository
import pl.gov.mc.protegosafe.domain.UserRepository

val dataModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
    single<TriageRepository> { TriageRepositoryImpl(get()) }
    single { UserIdStore(get()) }
    single { NotificationDataStore() }
    single { TriageDataStore(get()) }
    single { SharedPreferencesDelegates(androidApplication()) }

}