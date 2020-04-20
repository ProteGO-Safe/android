package pl.gov.mc.protegosafe.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.gov.mc.protegosafe.domain.PushNotifier
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.usecase.GetNotificationDataAndClearUseCase
import pl.gov.mc.protegosafe.domain.usecase.OnGetBridgeDataUseCase
import pl.gov.mc.protegosafe.domain.usecase.OnPushNotificationUseCase
import pl.gov.mc.protegosafe.domain.usecase.OnSetBridgeDataUseCase
import pl.gov.mc.protegosafe.domain.usecase.SaveNotificationDataUseCase
import pl.gov.mc.protegosafe.domain.usecase.StartBLEMonitoringServiceUseCase
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
    factory { OnGetBridgeDataUseCase(get()) }
    factory { OnSetBridgeDataUseCase(get(), get()) }
    factory { OnPushNotificationUseCase(get(), get()) }
    factory { SaveNotificationDataUseCase(get()) }
    factory { GetNotificationDataAndClearUseCase(get()) }
    factory { StartBLEMonitoringServiceUseCase(get()) }
}

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get()) }
    viewModel { MainViewModel(get()) }
}