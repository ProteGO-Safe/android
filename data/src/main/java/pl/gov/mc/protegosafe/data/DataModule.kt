package pl.gov.mc.protegosafe.data

import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Single
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import pl.gov.mc.protegosafe.data.db.*
import pl.gov.mc.protegosafe.domain.NotificationRepository
import pl.gov.mc.protegosafe.domain.TriageRepository
import pl.gov.mc.protegosafe.domain.UserRepository
import pl.gov.mc.protegosafe.domain.model.TriageData
import timber.log.Timber

val dataModule = module {
    single { ContactDatabase.buildDataBase(androidApplication()) }
    single { get<ContactDatabase>().contactDao() }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
    single<TriageRepository> { TriageRepositoryImpl(get()) }
    single { UserIdStore(get()) }
    single { NotificationDataStore() }
    single { TriageDataStore(get()) }
    single { SharedPreferencesDelegates(androidApplication()) }

}