package pl.gov.mc.protegosafe.data.di

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.datatheorem.android.trustkit.pinning.OkHttp3Helper
import com.google.android.gms.nearby.Nearby
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import pl.gov.mc.protegosafe.data.BuildConfig
import pl.gov.mc.protegosafe.data.Consts
import pl.gov.mc.protegosafe.data.KeyUploadSystemInfoRepositoryImpl
import pl.gov.mc.protegosafe.data.cloud.DiagnosisKeyDownloadService
import pl.gov.mc.protegosafe.data.cloud.UploadTemporaryExposureKeysService
import pl.gov.mc.protegosafe.data.db.NotificationDataStore
import pl.gov.mc.protegosafe.data.db.SafetyNetDataStore
import pl.gov.mc.protegosafe.data.db.SharedPreferencesDelegates
import pl.gov.mc.protegosafe.data.db.TriageDataStore
import pl.gov.mc.protegosafe.data.db.AppVersionDataStore
import pl.gov.mc.protegosafe.data.db.dao.ExposureDao
import pl.gov.mc.protegosafe.data.db.realm.RealmDatabaseBuilder
import pl.gov.mc.protegosafe.data.mapper.ApiExceptionMapperImpl
import pl.gov.mc.protegosafe.data.mapper.ClearMapperImpl
import pl.gov.mc.protegosafe.data.mapper.DiagnosisKeyDownloadConfigurationMapperImpl
import pl.gov.mc.protegosafe.data.mapper.ExposureConfigurationMapperImpl
import pl.gov.mc.protegosafe.data.mapper.PinMapperImpl
import pl.gov.mc.protegosafe.data.mapper.RiskLevelConfigurationMapperImpl
import pl.gov.mc.protegosafe.data.model.OutgoingBridgeDataResultComposerImpl
import pl.gov.mc.protegosafe.data.repository.DiagnosisKeyRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.ExposureNotificationRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.ExposureRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.NotificationRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.PendingActivityResultRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.CertificatePinningRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.MigrationRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.RemoteConfigurationRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.SafetyNetCheckRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.TemporaryExposureKeysUploadRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.TriageRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.WorkerStateRepositoryImpl
import pl.gov.mc.protegosafe.domain.model.ApiExceptionMapper
import pl.gov.mc.protegosafe.domain.model.ClearMapper
import pl.gov.mc.protegosafe.domain.model.ExposureConfigurationMapper
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.model.PinMapper
import pl.gov.mc.protegosafe.domain.model.DiagnosisKeyDownloadConfigurationMapper
import pl.gov.mc.protegosafe.domain.model.RiskLevelConfigurationMapper
import pl.gov.mc.protegosafe.domain.repository.DiagnosisKeyRepository
import pl.gov.mc.protegosafe.domain.repository.ExposureNotificationRepository
import pl.gov.mc.protegosafe.domain.repository.ExposureRepository
import pl.gov.mc.protegosafe.domain.repository.KeyUploadSystemInfoRepository
import pl.gov.mc.protegosafe.domain.repository.NotificationRepository
import pl.gov.mc.protegosafe.domain.repository.PendingActivityResultRepository
import pl.gov.mc.protegosafe.domain.repository.CertificatePinningRepository
import pl.gov.mc.protegosafe.domain.repository.MigrationRepository
import pl.gov.mc.protegosafe.domain.repository.RemoteConfigurationRepository
import pl.gov.mc.protegosafe.domain.repository.SafetyNetCheckRepository
import pl.gov.mc.protegosafe.domain.repository.TemporaryExposureKeysUploadRepository
import pl.gov.mc.protegosafe.domain.repository.TriageRepository
import pl.gov.mc.protegosafe.domain.repository.WorkerStateRepository
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single<Retrofit> { provideRetrofit() }
    single<DiagnosisKeyDownloadService> {
        get<Retrofit>().create(DiagnosisKeyDownloadService::class.java)
    }
    single<UploadTemporaryExposureKeysService> {
        get<Retrofit>().create(UploadTemporaryExposureKeysService::class.java)
    }
    single<NotificationRepository> { NotificationRepositoryImpl(get()) }
    single<TriageRepository> { TriageRepositoryImpl(get()) }
    single { NotificationDataStore() }
    single { TriageDataStore(get()) }
    single { SharedPreferencesDelegates(get()) }
    factory<ClearMapper> { ClearMapperImpl() }
    single { Nearby.getExposureNotificationClient(androidApplication()) }
    single<ExposureNotificationRepository> { ExposureNotificationRepositoryImpl(get(), get()) }
    single<RemoteConfigurationRepository> { RemoteConfigurationRepositoryImpl(get(), get(), get()) }
    factory<ExposureConfigurationMapper> { ExposureConfigurationMapperImpl() }
    factory<DiagnosisKeyDownloadConfigurationMapper> { DiagnosisKeyDownloadConfigurationMapperImpl() }
    factory<PinMapper> { PinMapperImpl() }
    factory<KeyUploadSystemInfoRepository> { KeyUploadSystemInfoRepositoryImpl(androidApplication()) }
    factory<OutgoingBridgeDataResultComposer> { OutgoingBridgeDataResultComposerImpl() }
    factory<ApiExceptionMapper> { ApiExceptionMapperImpl() }
    single<TemporaryExposureKeysUploadRepository> {
        TemporaryExposureKeysUploadRepositoryImpl(get())
    }
    single<PendingActivityResultRepository> { PendingActivityResultRepositoryImpl() }
    single { ExposureDao() }
    single<ExposureRepository> { ExposureRepositoryImpl(get()) }
    single<DiagnosisKeyRepository> { DiagnosisKeyRepositoryImpl(get(), get(), get(), get(), get()) }
    factory { RealmDatabaseBuilder(get(), get()) }
    single {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // TODO PSAFE-1074
            provideEncryptedSharedPreferences(androidContext())
        } else {
            provideRegularSharedPreferences(androidContext())
        }
    }
    single<WorkerStateRepository> { WorkerStateRepositoryImpl(get()) }
    single<CertificatePinningRepository> { CertificatePinningRepositoryImpl(get()) }
    single { SafetyNetDataStore(get()) }
    single<SafetyNetCheckRepository> { SafetyNetCheckRepositoryImpl(get()) }
    single<RiskLevelConfigurationMapper> { RiskLevelConfigurationMapperImpl() }
    single { AppVersionDataStore(get()) }
    single<MigrationRepository> { MigrationRepositoryImpl(get(), get(), get()) }
}

fun provideEncryptedSharedPreferences(context: Context) = EncryptedSharedPreferences.create(
    BuildConfig.SHARED_PREFERENCES_FILE_NAME,
    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
    context,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)

fun provideRegularSharedPreferences(context: Context): SharedPreferences =
    context.getSharedPreferences(
        BuildConfig.SHARED_PREFERENCES_FILE_NAME,
        Context.MODE_PRIVATE
    )

fun provideRetrofit(): Retrofit {
    val client = OkHttpClient.Builder().apply {
        sslSocketFactory(OkHttp3Helper.getSSLSocketFactory(), OkHttp3Helper.getTrustManager())
        addInterceptor(OkHttp3Helper.getPinningInterceptor())
        addInterceptor(HttpLoggingInterceptor().setLevel(
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        ))
        followSslRedirects(false)
        followRedirects(false)
    }.build()

    return Retrofit.Builder()
        .baseUrl(Consts.BASE_URL_FORMAT)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
}
