package pl.gov.mc.protegosafe.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.gov.mc.protegosafe.helpers.SetRiskHelperUseCase
import pl.gov.mc.protegosafe.helpers.SetWorkersIntervalUseCase
import pl.gov.mc.protegosafe.ui.TestHelpersViewModel

val helpers = module {
    factory { SetRiskHelperUseCase(get(), get(), get()) }
    viewModel { TestHelpersViewModel(get(), get()) }
    factory { SetWorkersIntervalUseCase(get(), get(), get()) }
}
