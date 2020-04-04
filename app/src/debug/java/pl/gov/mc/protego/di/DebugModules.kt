package pl.gov.mc.protego.di

import org.koin.core.module.Module
import org.koin.dsl.module
import pl.gov.mc.protego.ui.base.CockpitShakeDetector

val debugModule: Module = module {
    factory { CockpitShakeDetector() }
}

val variantSpecificModulesList = listOf(debugModule)
