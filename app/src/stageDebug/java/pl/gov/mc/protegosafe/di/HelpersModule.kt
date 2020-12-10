package pl.gov.mc.protegosafe.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.gov.mc.protegosafe.helpers.GetTemporaryExposureKeysUseCase
import pl.gov.mc.protegosafe.helpers.GetWebViewLoggingStatusUseCase
import pl.gov.mc.protegosafe.helpers.SetRiskHelperUseCase
import pl.gov.mc.protegosafe.helpers.SetWebViewLoggingEnabledUseCase
import pl.gov.mc.protegosafe.helpers.SetWorkersIntervalUseCase
import pl.gov.mc.protegosafe.ui.TestHelpersViewModel

val helpers = module {
    factory { SetRiskHelperUseCase(get(), get(), get()) }
    viewModel { TestHelpersViewModel(get(), get(), get(), get(), get()) }
    factory { SetWorkersIntervalUseCase(get(), get(), get()) }
    factory { SetWebViewLoggingEnabledUseCase(get(), get()) }
    factory { GetWebViewLoggingStatusUseCase(get(), get()) }
    factory { GetTemporaryExposureKeysUseCase(get(), get()) }
}
