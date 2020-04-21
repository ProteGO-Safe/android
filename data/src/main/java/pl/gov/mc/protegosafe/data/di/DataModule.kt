package pl.gov.mc.protegosafe.data.di

import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import pl.gov.mc.protegosafe.data.AuthRepositoryImpl
import pl.gov.mc.protegosafe.data.NotificationRepositoryImpl
import pl.gov.mc.protegosafe.data.TriageRepositoryImpl
import pl.gov.mc.protegosafe.data.UserRepositoryImpl
import pl.gov.mc.protegosafe.data.db.NotificationDataStore
import pl.gov.mc.protegosafe.data.db.SharedPreferencesDelegates
import pl.gov.mc.protegosafe.data.db.TriageDataStore
import pl.gov.mc.protegosafe.data.db.UserIdStore
import pl.gov.mc.protegosafe.domain.repository.AuthRepository
import pl.gov.mc.protegosafe.domain.repository.NotificationRepository
import pl.gov.mc.protegosafe.domain.repository.TriageRepository
import pl.gov.mc.protegosafe.domain.repository.UserRepository

val dataModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
    single<TriageRepository> { TriageRepositoryImpl(get()) }
    single { UserIdStore(get()) }
    single { NotificationDataStore() }
    single { TriageDataStore(get()) }
    single { SharedPreferencesDelegates(androidApplication()) }
    single { FirebaseAuth.getInstance() }
    single<AuthRepository> { AuthRepositoryImpl(get()) }

}