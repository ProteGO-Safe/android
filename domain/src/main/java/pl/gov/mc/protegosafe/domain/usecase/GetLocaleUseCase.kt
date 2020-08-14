package pl.gov.mc.protegosafe.domain.usecase

import pl.gov.mc.protegosafe.domain.repository.AppRepository
import java.util.Locale

class GetLocaleUseCase(
    private val appRepository: AppRepository
) {
    fun execute(): Locale {
        return appRepository.getLocale()
    }
}
