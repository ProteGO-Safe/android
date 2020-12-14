package pl.gov.mc.protegosafe.data.di

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.datatheorem.android.trustkit.pinning.OkHttp3Helper
import com.facebook.stetho.okhttp3.StethoInterceptor
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
import pl.gov.mc.protegosafe.data.cloud.CovidInfoService
import pl.gov.mc.protegosafe.data.cloud.CovidTestService
import pl.gov.mc.protegosafe.data.cloud.UploadTemporaryExposureKeysService
import pl.gov.mc.protegosafe.data.db.AppLanguageDataStore
import pl.gov.mc.protegosafe.data.db.RouteDataStore
import pl.gov.mc.protegosafe.data.db.SafetyNetDataStore
import pl.gov.mc.protegosafe.data.db.SharedPreferencesDelegates
import pl.gov.mc.protegosafe.data.db.TriageDataStore
import pl.gov.mc.protegosafe.data.db.AppVersionDataStore
import pl.gov.mc.protegosafe.data.db.CovidInfoDataStore
import pl.gov.mc.protegosafe.data.db.WebViewLoggingDataStore
import pl.gov.mc.protegosafe.data.db.WorkersIntervalDataStore
import pl.gov.mc.protegosafe.data.db.dao.ActivitiesDao
import pl.gov.mc.protegosafe.data.db.dao.ExposureDao
import pl.gov.mc.protegosafe.data.db.dao.CovidInfoDao
import pl.gov.mc.protegosafe.data.db.dao.DiagnosisKeyDao
import pl.gov.mc.protegosafe.data.db.realm.RealmDatabaseBuilder
import pl.gov.mc.protegosafe.data.mapper.ApiExceptionMapperImpl
import pl.gov.mc.protegosafe.data.mapper.DiagnosisKeyDownloadConfigurationMapperImpl
import pl.gov.mc.protegosafe.data.mapper.ExposureConfigurationMapperImpl
import pl.gov.mc.protegosafe.data.mapper.IncomingBridgePayloadMapperImpl
import pl.gov.mc.protegosafe.data.mapper.OutgoingBridgePayloadMapperImpl
import pl.gov.mc.protegosafe.data.mapper.PinMapperImpl
import pl.gov.mc.protegosafe.data.mapper.RetrofitExceptionMapperImpl
import pl.gov.mc.protegosafe.data.mapper.RiskLevelConfigurationMapperImpl
import pl.gov.mc.protegosafe.data.db.dao.CovidTestDao
import pl.gov.mc.protegosafe.data.mapper.FcmNotificationMapperImpl
import pl.gov.mc.protegosafe.data.mapper.TestSubscriptionConfigurationMapperImpl
import pl.gov.mc.protegosafe.data.model.OutgoingBridgeDataResultComposerImpl
import pl.gov.mc.protegosafe.data.repository.ActivitiesRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.CacheStoreImpl
import pl.gov.mc.protegosafe.data.repository.DiagnosisKeyRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.ExposureNotificationRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.ExposureRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.RouteRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.PendingActivityResultRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.CertificatePinningRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.MigrationRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.RemoteConfigurationRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.CovidInfoRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.CovidTestRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.ProtobufRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.SafetyNetRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.TemporaryExposureKeysUploadRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.TriageRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.UiRequestCacheRepositoryImpl
import pl.gov.mc.protegosafe.data.repository.WorkerStateRepositoryImpl
import pl.gov.mc.protegosafe.domain.model.ApiExceptionMapper
import pl.gov.mc.protegosafe.domain.model.ExposureConfigurationMapper
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.model.PinMapper
import pl.gov.mc.protegosafe.domain.model.DiagnosisKeyDownloadConfigurationMapper
import pl.gov.mc.protegosafe.domain.model.FcmNotificationMapper
import pl.gov.mc.protegosafe.domain.model.IncomingBridgePayloadMapper
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgePayloadMapper
import pl.gov.mc.protegosafe.domain.model.RetrofitExceptionMapper
import pl.gov.mc.protegosafe.domain.model.RiskLevelConfigurationMapper
import pl.gov.mc.protegosafe.domain.model.TestSubscriptionConfigurationMapper
import pl.gov.mc.protegosafe.domain.repository.ActivitiesRepository
import pl.gov.mc.protegosafe.domain.repository.CacheStore
import pl.gov.mc.protegosafe.domain.repository.DiagnosisKeyRepository
import pl.gov.mc.protegosafe.domain.repository.ExposureNotificationRepository
import pl.gov.mc.protegosafe.domain.repository.ExposureRepository
import pl.gov.mc.protegosafe.domain.repository.KeyUploadSystemInfoRepository
import pl.gov.mc.protegosafe.domain.repository.RouteRepository
import pl.gov.mc.protegosafe.domain.repository.PendingActivityResultRepository
import pl.gov.mc.protegosafe.domain.repository.CertificatePinningRepository
import pl.gov.mc.protegosafe.domain.repository.MigrationRepository
import pl.gov.mc.protegosafe.domain.repository.RemoteConfigurationRepository
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository
import pl.gov.mc.protegosafe.domain.repository.CovidTestRepository
import pl.gov.mc.protegosafe.domain.repository.ProtobufRepository
import pl.gov.mc.protegosafe.domain.repository.SafetyNetRepository
import pl.gov.mc.protegosafe.domain.repository.TemporaryExposureKeysUploadRepository
import pl.gov.mc.protegosafe.domain.repository.TriageRepository
import pl.gov.mc.protegosafe.domain.repository.UiRequestCacheRepository
import pl.gov.mc.protegosafe.domain.repository.WorkerStateRepository
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

val dataModule = module {
    single<Retrofit> { provideRetrofit() }
    single<DiagnosisKeyDownloadService> {
        get<Retrofit>().create(DiagnosisKeyDownloadService::class.java)
    }
    single<UploadTemporaryExposureKeysService> {
        get<Retrofit>().create(UploadTemporaryExposureKeysService::class.java)
    }
    single<CovidInfoService> {
        get<Retrofit>().create(CovidInfoService::class.java)
    }
    single<CovidTestService> {
        get<Retrofit>().create(CovidTestService::class.java)
    }
    single<RouteRepository> { RouteRepositoryImpl(get()) }
    single<TriageRepository> { TriageRepositoryImpl(get()) }
    single { RouteDataStore() }
    single { TriageDataStore(get()) }
    single { SharedPreferencesDelegates(get()) }
    single { Nearby.getExposureNotificationClient(androidApplication()) }
    single<ExposureNotificationRepository> { ExposureNotificationRepositoryImpl(get(), get()) }
    single<RemoteConfigurationRepository> { RemoteConfigurationRepositoryImpl(get(), get(), get(), get()) }
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
    single<ExposureRepository> { ExposureRepositoryImpl(get(), get()) }
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
    single<SafetyNetRepository> { SafetyNetRepositoryImpl(get(), get()) }
    single<RiskLevelConfigurationMapper> { RiskLevelConfigurationMapperImpl() }
    single { AppVersionDataStore(get()) }
    single<MigrationRepository> { MigrationRepositoryImpl(get(), get(), get()) }
    single<RetrofitExceptionMapper> { RetrofitExceptionMapperImpl() }
    single<IncomingBridgePayloadMapper> { IncomingBridgePayloadMapperImpl(get()) }
    single { AppLanguageDataStore(get()) }
    single<CovidInfoRepository> { CovidInfoRepositoryImpl(get(), get(), get(), get()) }
    single { CovidInfoDao() }
    single { CovidInfoDataStore(get()) }
    single<OutgoingBridgePayloadMapper> { OutgoingBridgePayloadMapperImpl() }
    single { DiagnosisKeyDao() }
    single { CovidTestDao() }
    single<CovidTestRepository> { CovidTestRepositoryImpl(get(), get(), get()) }
    single<TestSubscriptionConfigurationMapper> { TestSubscriptionConfigurationMapperImpl() }
    single<UiRequestCacheRepository> { UiRequestCacheRepositoryImpl(get(), get(), get()) }
    single<CacheStore> { CacheStoreImpl() }
    single { WorkersIntervalDataStore(get()) }
    single { WebViewLoggingDataStore(get()) }
    single<FcmNotificationMapper> { FcmNotificationMapperImpl(get()) }
    single<ProtobufRepository> { ProtobufRepositoryImpl() }
    single { ActivitiesDao() }
    single<ActivitiesRepository> { ActivitiesRepositoryImpl(get()) }
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
        if (BuildConfig.DEBUG) {
            addInterceptor(
                HttpLoggingInterceptor {
                    Timber.tag("OkHttp").d(it)
                }.apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            addNetworkInterceptor(StethoInterceptor())
        }
        followSslRedirects(false)
        followRedirects(false)
        connectTimeout(DEFAULT_TIMEOUT_SEC, TimeUnit.SECONDS)
        readTimeout(DEFAULT_TIMEOUT_SEC, TimeUnit.SECONDS)
        writeTimeout(DEFAULT_TIMEOUT_SEC, TimeUnit.SECONDS)
    }.build()

    return Retrofit.Builder()
        .baseUrl(Consts.BASE_URL_FORMAT)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
}

private const val DEFAULT_TIMEOUT_SEC = 40L
