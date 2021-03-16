package pl.gov.mc.protegosafe.di

import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import io.realm.Realm
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.gov.mc.protegosafe.AppRepositoryImpl
import pl.gov.mc.protegosafe.domain.Notifier
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.repository.AppRepository
import pl.gov.mc.protegosafe.domain.repository.FileRepository
import pl.gov.mc.protegosafe.domain.usecase.AppReviewUseCase
import pl.gov.mc.protegosafe.domain.usecase.CancelExposureRiskUseCase
import pl.gov.mc.protegosafe.domain.usecase.ChangeServiceStatusUseCase
import pl.gov.mc.protegosafe.domain.usecase.CheckDeviceRootedUseCase
import pl.gov.mc.protegosafe.domain.usecase.ClearDataUseCase
import pl.gov.mc.protegosafe.domain.usecase.CloseAppUseCase
import pl.gov.mc.protegosafe.domain.usecase.ComposeAppLifecycleStateBrideDataUseCase
import pl.gov.mc.protegosafe.domain.usecase.DeleteActivitiesUseCase
import pl.gov.mc.protegosafe.domain.usecase.GetActivitiesResultUseCase
import pl.gov.mc.protegosafe.domain.usecase.GetAnalyzeResultUseCase
import pl.gov.mc.protegosafe.domain.usecase.GetAppVersionNameUseCase
import pl.gov.mc.protegosafe.domain.usecase.GetCovidStatsNotificationStatusResultUseCase
import pl.gov.mc.protegosafe.domain.usecase.GetENStatsResultUseCase
import pl.gov.mc.protegosafe.domain.usecase.GetExposureInformationUseCase
import pl.gov.mc.protegosafe.domain.usecase.GetFontScaleUseCase
import pl.gov.mc.protegosafe.domain.usecase.GetLocaleUseCase
import pl.gov.mc.protegosafe.domain.usecase.GetMigrationUrlUseCase
import pl.gov.mc.protegosafe.domain.usecase.GetRouteDataAndClearUseCase
import pl.gov.mc.protegosafe.domain.usecase.GetServicesStatusUseCase
import pl.gov.mc.protegosafe.domain.usecase.GetSystemLanguageUseCase
import pl.gov.mc.protegosafe.domain.usecase.HandleNewUriUseCase
import pl.gov.mc.protegosafe.domain.usecase.OnGetBridgeDataUseCase
import pl.gov.mc.protegosafe.domain.usecase.OnSetBridgeDataUseCase
import pl.gov.mc.protegosafe.domain.usecase.PrepareMigrationIfRequiredUseCase
import pl.gov.mc.protegosafe.domain.usecase.ProcessPendingActivityResultUseCase
import pl.gov.mc.protegosafe.domain.usecase.ProvideDiagnosisKeysUseCase
import pl.gov.mc.protegosafe.domain.usecase.RescheduleProvideDiagnosisKeysTaskUseCase
import pl.gov.mc.protegosafe.domain.usecase.SaveExposureCheckActivityUseCase
import pl.gov.mc.protegosafe.domain.usecase.SaveExposureUseCase
import pl.gov.mc.protegosafe.domain.usecase.SaveKeysCountToAnalyzeUseCase
import pl.gov.mc.protegosafe.domain.usecase.SaveRiskCheckActivityUseCase
import pl.gov.mc.protegosafe.domain.usecase.SaveRouteUseCase
import pl.gov.mc.protegosafe.domain.usecase.SaveTriageCompletedUseCase
import pl.gov.mc.protegosafe.domain.usecase.SetAppLanguageUseCase
import pl.gov.mc.protegosafe.domain.usecase.ShowPushNotificationUseCase
import pl.gov.mc.protegosafe.domain.usecase.StartExposureNotificationUseCase
import pl.gov.mc.protegosafe.domain.usecase.StopExposureNotificationUseCase
import pl.gov.mc.protegosafe.domain.usecase.StorePendingActivityResultUseCase
import pl.gov.mc.protegosafe.domain.usecase.SubscribeCovidStatusTopicUseCase
import pl.gov.mc.protegosafe.domain.usecase.UpdateCovidStatsNotificationsStatusUseCase
import pl.gov.mc.protegosafe.domain.usecase.UploadTemporaryExposureKeysUseCase
import pl.gov.mc.protegosafe.domain.usecase.UploadTemporaryExposureKeysWithCachedPayloadUseCase
import pl.gov.mc.protegosafe.domain.usecase.covidtest.GetTestSubscriptionPinUseCase
import pl.gov.mc.protegosafe.domain.usecase.covidtest.GetTestSubscriptionStatusUseCase
import pl.gov.mc.protegosafe.domain.usecase.covidtest.UpdateTestSubscriptionStatusUseCase
import pl.gov.mc.protegosafe.domain.usecase.covidtest.UploadTestSubscriptionPinUseCase
import pl.gov.mc.protegosafe.domain.usecase.info.UpdateDashboardIfRequiredAndGetResultUseCase
import pl.gov.mc.protegosafe.domain.usecase.info.UpdateDashboardUseCase
import pl.gov.mc.protegosafe.domain.usecase.info.UpdateDetailsIfRequiredAndGetResultUseCase
import pl.gov.mc.protegosafe.domain.usecase.info.UpdateTimestampsIfRequiredAndGetUseCase
import pl.gov.mc.protegosafe.domain.usecase.restrictions.GetSubscribedDistrictsResultUseCase
import pl.gov.mc.protegosafe.domain.usecase.restrictions.GetVoivodeshipsResultOrFetchIfRequiredUseCase
import pl.gov.mc.protegosafe.domain.usecase.restrictions.GetVoivodeshipsResultUseCase
import pl.gov.mc.protegosafe.domain.usecase.restrictions.HandleDistrictActionUseCase
import pl.gov.mc.protegosafe.domain.usecase.restrictions.NotifyDistrictsUpdatedUseCase
import pl.gov.mc.protegosafe.domain.usecase.restrictions.UpdateVoivodeshipsAndSyncDistrictsUseCase
import pl.gov.mc.protegosafe.domain.usecase.restrictions.UpdateVoivodeshipsIfRequiredUseCase
import pl.gov.mc.protegosafe.repository.FileRepositoryImpl
import pl.gov.mc.protegosafe.ui.MainViewModel
import pl.gov.mc.protegosafe.ui.common.NotifierImpl
import pl.gov.mc.protegosafe.ui.home.HomeViewModel
import pl.gov.mc.protegosafe.ui.home.WebUrlProvider

val appModule = module {
    factory<Notifier> { NotifierImpl(get(), get()) }
    factory { WebUrlProvider(get()) }
    factory<PostExecutionThread> { pl.gov.mc.protegosafe.executor.PostExecutionThread() }
    factory { Realm.getDefaultInstance() }
    single<AppUpdateManager> { AppUpdateManagerFactory.create(androidContext()) }
    single<AppRepository> { AppRepositoryImpl(get(), get(), get(), get(), get(), androidContext()) }
    factory<FileRepository> { FileRepositoryImpl(androidContext()) }
}

val useCaseModule = module {
    factory {
        OnGetBridgeDataUseCase(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    factory {
        OnSetBridgeDataUseCase(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    factory { ShowPushNotificationUseCase(get(), get()) }
    factory { SaveRouteUseCase(get(), get()) }
    factory { GetRouteDataAndClearUseCase(get()) }
    factory { StartExposureNotificationUseCase(get(), get(), get()) }
    factory { StopExposureNotificationUseCase(get(), get(), get()) }
    factory { GetServicesStatusUseCase(get()) }
    factory { ChangeServiceStatusUseCase(get(), get(), get()) }
    factory { ClearDataUseCase(get(), get(), get(), get()) }
    factory { ProvideDiagnosisKeysUseCase(get(), get(), get()) }
    factory {
        UploadTemporaryExposureKeysUseCase(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    factory { UploadTemporaryExposureKeysWithCachedPayloadUseCase(get(), get(), get()) }
    factory { SaveTriageCompletedUseCase(get(), get()) }
    factory { ComposeAppLifecycleStateBrideDataUseCase(get()) }
    factory { SaveExposureUseCase(get(), get()) }
    factory { StorePendingActivityResultUseCase(get(), get()) }
    factory { ProcessPendingActivityResultUseCase(get(), get()) }
    factory { GetExposureInformationUseCase(get(), get()) }
    factory { GetAnalyzeResultUseCase(get(), get(), get(), get()) }
    factory { CheckDeviceRootedUseCase(get(), get(), get(), get()) }
    factory { PrepareMigrationIfRequiredUseCase(get(), get()) }
    factory { GetMigrationUrlUseCase(get()) }
    factory { GetAppVersionNameUseCase(get(), get(), get()) }
    factory { GetSystemLanguageUseCase(get(), get(), get()) }
    factory { SetAppLanguageUseCase(get(), get(), get()) }
    factory { GetLocaleUseCase(get()) }
    factory { GetFontScaleUseCase(get(), get(), get()) }
    factory { CloseAppUseCase(get(), get()) }
    factory { HandleDistrictActionUseCase(get(), get(), get()) }
    factory { GetSubscribedDistrictsResultUseCase(get(), get(), get()) }
    factory { NotifyDistrictsUpdatedUseCase(get(), get(), get(), get()) }
    factory { UploadTestSubscriptionPinUseCase(get(), get(), get(), get(), get(), get()) }
    factory { GetTestSubscriptionStatusUseCase(get(), get(), get(), get()) }
    factory { UpdateTestSubscriptionStatusUseCase(get(), get(), get()) }
    factory { GetTestSubscriptionPinUseCase(get(), get(), get()) }
    factory { CancelExposureRiskUseCase(get(), get(), get()) }
    factory { AppReviewUseCase(get(), get()) }
    factory { SaveKeysCountToAnalyzeUseCase(get(), get(), get(), get()) }
    factory { SaveRiskCheckActivityUseCase(get(), get()) }
    factory { SaveExposureCheckActivityUseCase(get(), get(), get()) }
    factory { GetActivitiesResultUseCase(get(), get(), get()) }
    factory { DeleteActivitiesUseCase(get(), get(), get()) }
    factory { HandleNewUriUseCase(get(), get()) }
    factory { GetCovidStatsNotificationStatusResultUseCase(get(), get(), get()) }
    factory { UpdateCovidStatsNotificationsStatusUseCase(get(), get(), get(), get()) }
    factory { SubscribeCovidStatusTopicUseCase(get(), get()) }
    factory { GetENStatsResultUseCase(get(), get(), get()) }
    factory { RescheduleProvideDiagnosisKeysTaskUseCase(get(), get()) }
    factory { UpdateTimestampsIfRequiredAndGetUseCase(get(), get()) }
    factory { UpdateDashboardUseCase(get(), get()) }
    factory { UpdateDashboardIfRequiredAndGetResultUseCase(get(), get(), get(), get()) }
    factory { UpdateDetailsIfRequiredAndGetResultUseCase(get(), get(), get()) }
    factory { GetVoivodeshipsResultUseCase(get(), get(), get()) }
    factory { GetVoivodeshipsResultOrFetchIfRequiredUseCase(get(), get(), get(), get()) }
    factory { UpdateVoivodeshipsAndSyncDistrictsUseCase(get(), get(), get()) }
    factory { UpdateVoivodeshipsIfRequiredUseCase(get(), get(), get(), get()) }
}

val viewModelModule = module {
    viewModel { MainViewModel(get(), get(), get(), get()) }
    viewModel {
        HomeViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
}
