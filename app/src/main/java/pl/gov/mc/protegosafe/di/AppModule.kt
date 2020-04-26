package pl.gov.mc.protegosafe.di

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.gov.mc.protegosafe.domain.PushNotifier
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.repository.ToastRepository
import pl.gov.mc.protegosafe.domain.usecase.EnableBTServiceUseCase
import pl.gov.mc.protegosafe.domain.usecase.GetInternetConnectionStatusUseCase
import pl.gov.mc.protegosafe.domain.usecase.GetNotificationDataAndClearUseCase
import pl.gov.mc.protegosafe.domain.usecase.GetSafetyNetNonceDataUseCase
import pl.gov.mc.protegosafe.domain.usecase.GetServicesStatusUseCase
import pl.gov.mc.protegosafe.domain.usecase.GetTemporaryIDUseCase
import pl.gov.mc.protegosafe.domain.usecase.GetTrackingAgreementStatusUseCase
import pl.gov.mc.protegosafe.domain.usecase.OnGetBridgeDataUseCase
import pl.gov.mc.protegosafe.domain.usecase.OnPushNotificationUseCase
import pl.gov.mc.protegosafe.domain.usecase.OnSetBridgeDataUseCase
import pl.gov.mc.protegosafe.domain.usecase.SaveNotificationDataUseCase
import pl.gov.mc.protegosafe.domain.usecase.SetBroadcastMessageUseCase
import pl.gov.mc.protegosafe.domain.usecase.SignInAndStartBLEMonitoringServiceUseCase
import pl.gov.mc.protegosafe.domain.usecase.StartBLEMonitoringServiceUseCase
import pl.gov.mc.protegosafe.domain.usecase.StopBLEMonitoringServiceUseCase
import pl.gov.mc.protegosafe.domain.usecase.auth.SignInUseCase
import pl.gov.mc.protegosafe.repository.ToastRepositoryImpl
import pl.gov.mc.protegosafe.ui.MainViewModel
import pl.gov.mc.protegosafe.ui.common.PushNotifierImpl
import pl.gov.mc.protegosafe.ui.home.HomeViewModel
import pl.gov.mc.protegosafe.ui.home.WebUrlProvider

val appModule = module {
    factory<PushNotifier> { PushNotifierImpl(get()) }
    factory { WebUrlProvider(get()) }
    factory<PostExecutionThread> { pl.gov.mc.protegosafe.executor.PostExecutionThread() }
    single<ToastRepository> { ToastRepositoryImpl(androidApplication()) }
}

val useCaseModule = module {
    factory { OnGetBridgeDataUseCase(get(), get()) }
    factory { OnSetBridgeDataUseCase(get(), get(), get(), get(), get()) }
    factory { OnPushNotificationUseCase(get(), get()) }
    factory { SaveNotificationDataUseCase(get()) }
    factory { GetNotificationDataAndClearUseCase(get()) }
    factory { StartBLEMonitoringServiceUseCase(get()) }
    factory { StopBLEMonitoringServiceUseCase(get()) }
    factory { SetBroadcastMessageUseCase(get()) }
    factory { GetTemporaryIDUseCase(get(), get()) }
    factory { SignInUseCase(get(), get(), get()) }
    factory { SignInAndStartBLEMonitoringServiceUseCase(get(), get(), get()) }
    factory { GetSafetyNetNonceDataUseCase(get())}
    factory { GetInternetConnectionStatusUseCase(get()) }
    factory { GetServicesStatusUseCase(get()) }
    factory { GetTrackingAgreementStatusUseCase(get()) }
    factory { EnableBTServiceUseCase(get(), get(), get(), get(), get()) }
}

val viewModelModule = module {
    viewModel { MainViewModel(get(), get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(), get()) }
}