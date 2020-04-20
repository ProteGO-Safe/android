package pl.gov.mc.protegosafe.ui

import pl.gov.mc.protegosafe.domain.usecase.SaveNotificationDataUseCase
import pl.gov.mc.protegosafe.ui.common.BaseViewModel

class MainViewModel(
    private val saveNotificationDataUseCase: SaveNotificationDataUseCase
): BaseViewModel() {

    fun onNotificationDataReceived(data: String) {
        saveNotificationDataUseCase.execute(data)
    }
}