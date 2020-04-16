package se.sigmaconnectivity.blescanner.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import se.sigmaconnectivity.blescanner.domain.PushNotifier
import se.sigmaconnectivity.blescanner.domain.executor.PostExecutionThread
import se.sigmaconnectivity.blescanner.domain.usecase.GetNotificationDataAndClearUseCase
import se.sigmaconnectivity.blescanner.domain.usecase.OnGetBridgeDataUseCase
import se.sigmaconnectivity.blescanner.domain.usecase.OnPushNotificationUseCase
import se.sigmaconnectivity.blescanner.domain.usecase.OnSetBridgeDataUseCase
import se.sigmaconnectivity.blescanner.ui.MainViewModel
import se.sigmaconnectivity.blescanner.ui.common.PushNotifierImpl
import se.sigmaconnectivity.blescanner.ui.home.HomeViewModel

val appModule = module {
    factory<PushNotifier> { PushNotifierImpl(get()) }

    factory { OnGetBridgeDataUseCase(get()) }
    factory { OnSetBridgeDataUseCase(get()) }
    factory { OnPushNotificationUseCase(get(), get(), get()) }
    factory { GetNotificationDataAndClearUseCase(get()) }

    factory<PostExecutionThread> { se.sigmaconnectivity.blescanner.executor.PostExecutionThread() }
}

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get()) }
    viewModel { MainViewModel() }
}