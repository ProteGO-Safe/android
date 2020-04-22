package pl.gov.mc.protegosafe.domain.usecase

import pl.gov.mc.protegosafe.domain.manager.IInternetConnectionManager

class GetInternetConnectionStatusUseCase(
    private val internetConnectionManager: IInternetConnectionManager
) {
    fun execute(): IInternetConnectionManager.InternetConnectionStatus =
        internetConnectionManager.getInternetConnectionStatus()
}