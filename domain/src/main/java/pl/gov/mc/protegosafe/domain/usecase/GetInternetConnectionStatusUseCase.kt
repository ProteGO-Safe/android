package pl.gov.mc.protegosafe.domain.usecase

import pl.gov.mc.protegosafe.domain.manager.InternetConnectionManager

class GetInternetConnectionStatusUseCase(
    private val internetConnectionManager: InternetConnectionManager
) {
    fun execute(): InternetConnectionManager.InternetConnectionStatus =
        internetConnectionManager.getInternetConnectionStatus()
}