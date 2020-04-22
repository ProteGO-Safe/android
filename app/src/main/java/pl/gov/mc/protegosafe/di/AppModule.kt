package pl.gov.mc.protegosafe.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.gov.mc.protegosafe.domain.PushNotifier
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.usecase.*
import pl.gov.mc.protegosafe.domain.usecase.auth.SignInUseCase
import pl.gov.mc.protegosafe.ui.MainViewModel
import pl.gov.mc.protegosafe.ui.common.PushNotifierImpl
import pl.gov.mc.protegosafe.ui.home.HomeViewModel
import pl.gov.mc.protegosafe.ui.home.WebUrlProvider

val appModule = module {
    factory<PushNotifier> { PushNotifierImpl(get()) }
    factory { WebUrlProvider(get()) }
    factory<PostExecutionThread> { pl.gov.mc.protegosafe.executor.PostExecutionThread() }
}

val useCaseModule = module {
    factory { OnGetBridgeDataUseCase(get(), get()) }
    factory { OnSetBridgeDataUseCase(get(), get()) }
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
    factory { GetServicesStatusUseCase() }
}

val viewModelModule = module {
    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { HomeViewModel(get(), get(), get()) }
}