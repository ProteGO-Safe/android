package pl.gov.mc.protegosafe.di

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.gov.mc.protegosafe.domain.DumpTraceDataUseCase
import pl.gov.mc.protegosafe.domain.PushNotifier
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.repository.ToastRepository
import pl.gov.mc.protegosafe.domain.usecase.*
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
    factory { OnGetBridgeDataUseCase(get(), get(), get()) }
    factory { OnSetBridgeDataUseCase(get(), get(), get(), get(), get(), get(), get()) }
    factory { OnPushNotificationUseCase(get(), get()) }
    factory { SaveNotificationDataUseCase(get()) }
    factory { GetNotificationDataAndClearUseCase(get()) }
    factory { StartBLEMonitoringServiceUseCase(get()) }
    factory { StopBLEMonitoringServiceUseCase(get()) }
    factory { SetBroadcastMessageUseCase(get()) }
    factory { SignInUseCase(get(), get()) }
    factory { SignInAndStartBLEMonitoringServiceUseCase(get(), get()) }
    factory { GetInternetConnectionStatusUseCase(get()) }
    factory { GetServicesStatusUseCase(get()) }
    factory { GetTrackingAgreementStatusUseCase(get()) }
    factory { EnableBTServiceUseCase(get(), get(), get(), get()) }
    factory { ClearBtDataUseCase(get(), get()) }
    factory { DumpTraceDataUseCase(get(), get()) }
    factory { TrackTempIdUseCase(get(), get()) }
    factory { GetCurrentTemporaryIDUseCase(get()) }
}

val viewModelModule = module {
    viewModel { MainViewModel(get(), get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get(), get(), get(), get()) }
}