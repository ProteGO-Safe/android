package se.sigmaconnectivity.blescanner.data

import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import se.sigmaconnectivity.blescanner.data.db.ContactDatabase
import se.sigmaconnectivity.blescanner.data.db.NotificationDataStore
import se.sigmaconnectivity.blescanner.data.db.SharedPreferencesDelegates
import se.sigmaconnectivity.blescanner.data.db.TriageDataStore
import se.sigmaconnectivity.blescanner.data.db.UserIdStore
import se.sigmaconnectivity.blescanner.domain.NotificationRepository
import se.sigmaconnectivity.blescanner.domain.TriageRepository
import se.sigmaconnectivity.blescanner.domain.UserRepository

val dataModule = module {
    single { ContactDatabase.buildDataBase(androidApplication()) }
    single { get<ContactDatabase>().contactDao() }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
    single<TriageRepository> { TriageRepositoryImpl(get()) }
    single { UserIdStore(get()) }
    single { NotificationDataStore(get()) }
    single { TriageDataStore(get()) }
    single { SharedPreferencesDelegates(androidApplication()) }

}