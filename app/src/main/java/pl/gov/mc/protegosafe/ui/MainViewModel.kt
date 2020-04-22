package pl.gov.mc.protegosafe.ui

import io.reactivex.rxkotlin.addTo
import pl.gov.mc.protegosafe.domain.usecase.SaveNotificationDataUseCase
import pl.gov.mc.protegosafe.domain.usecase.SignInAndStartBLEMonitoringServiceUseCase
import pl.gov.mc.protegosafe.domain.usecase.StartBLEMonitoringServiceUseCase
import pl.gov.mc.protegosafe.domain.usecase.auth.SignInUseCase
import pl.gov.mc.protegosafe.ui.common.BaseViewModel
import timber.log.Timber

class MainViewModel(
    private val saveNotificationDataUseCase: SaveNotificationDataUseCase,
    signInAndStartBLEMonitoringServiceUseCase: SignInAndStartBLEMonitoringServiceUseCase
): BaseViewModel() {

    init {
        signInAndStartBLEMonitoringServiceUseCase.execute()
            .subscribe({
                Timber.d("Service init completed")
            }, {
                Timber.e(it, "Service init failed")
            })
            .addTo(disposables)
    }

    fun onNotificationDataReceived(data: String) {
        saveNotificationDataUseCase.execute(data)
    }

}