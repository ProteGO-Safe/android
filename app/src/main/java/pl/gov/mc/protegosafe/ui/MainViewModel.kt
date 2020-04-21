package pl.gov.mc.protegosafe.ui

import io.reactivex.rxkotlin.addTo
import org.koin.core.KoinComponent
import org.koin.core.inject
import pl.gov.mc.protegosafe.domain.usecase.IGetInternetConnectionStatusUseCase
import pl.gov.mc.protegosafe.domain.usecase.SaveNotificationDataUseCase
import pl.gov.mc.protegosafe.domain.usecase.SignInAndStartBLEMonitoringServiceUseCase
import pl.gov.mc.protegosafe.manager.SafetyNetManager
import pl.gov.mc.protegosafe.ui.common.BaseViewModel
import timber.log.Timber

class MainViewModel(
    private val saveNotificationDataUseCase: SaveNotificationDataUseCase,
    signInAndStartBLEMonitoringServiceUseCase: SignInAndStartBLEMonitoringServiceUseCase,
    private val getInternetConnectionStatusUseCase: IGetInternetConnectionStatusUseCase
): BaseViewModel(), KoinComponent {

    private val safetyNetManager: SafetyNetManager by inject()

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

    fun isInternetConnectionAvailable() = getInternetConnectionStatusUseCase.execute().isConnected()

    fun getSafetyNetResultData() = safetyNetManager.safetyNetResult

    fun startSafetyNetVerification() = safetyNetManager.startDeviceVerification()
}