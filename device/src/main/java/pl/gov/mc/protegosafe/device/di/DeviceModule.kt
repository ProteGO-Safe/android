package pl.gov.mc.protegosafe.device.di

import androidx.work.WorkManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import pl.gov.mc.protegosafe.device.repository.DeviceRepositoryImpl
import pl.gov.mc.protegosafe.device.BuildConfig
import pl.gov.mc.protegosafe.domain.manager.InternetConnectionManager
import pl.gov.mc.protegosafe.domain.manager.KeystoreManager
import pl.gov.mc.protegosafe.domain.manager.SafetyNetAttestationWrapper
import pl.gov.mc.protegosafe.domain.model.ChangeServiceStatusRequestMapper
import pl.gov.mc.protegosafe.domain.repository.DeviceRepository
import pl.gov.mc.protegosafe.domain.scheduler.ApplicationTaskScheduler
import pl.gov.mc.protegosafe.device.manager.InternetConnectionManagerImpl
import pl.gov.mc.protegosafe.device.manager.KeystoreManagerImpl
import pl.gov.mc.protegosafe.device.manager.SafetyNetAttestationWrapperImpl
import pl.gov.mc.protegosafe.device.mapper.ChangeServiceStatusRequestMapperImpl
import pl.gov.mc.protegosafe.device.scheduler.ApplicationTaskSchedulerImpl
import pl.gov.mc.protegosafe.device.scheduler.ProvideDiagnosisKeyWorker
import pl.gov.mc.protegosafe.device.scheduler.RemoveOldExposuresWorker
import pl.gov.mc.protegosafe.device.scheduler.UpdateCovidInfoWorker

val deviceModule = module {
    single<InternetConnectionManager> {
        InternetConnectionManagerImpl(
            context = androidContext()
        )
    }
    single<KeystoreManager> { KeystoreManagerImpl() }
    single<DeviceRepository> { DeviceRepositoryImpl(androidContext(), get()) }
    factory<ChangeServiceStatusRequestMapper> { ChangeServiceStatusRequestMapperImpl() }
    factory {
        WorkManager.getInstance(androidContext())
    }
    single<SafetyNetAttestationWrapper> {
        SafetyNetAttestationWrapperImpl(
            context = androidContext(),
            safetyNetApiKey = BuildConfig.SAFETYNET_API_KEY
        )
    }
    single<ApplicationTaskScheduler> {
        ApplicationTaskSchedulerImpl(
            appRepository = get(),
            workManager = get(),
            provideDiagnosisKeyWorker = ProvideDiagnosisKeyWorker::class.java,
            removeOldExposuresWorker = RemoveOldExposuresWorker::class.java,
            updateDistrictsRestrictionsWorker = UpdateCovidInfoWorker::class.java
        )
    }
}
