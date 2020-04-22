package pl.gov.mc.protegosafe.ui

import io.reactivex.rxkotlin.addTo
import pl.gov.mc.protegosafe.domain.usecase.SaveNotificationDataUseCase
import pl.gov.mc.protegosafe.domain.usecase.auth.SignInUseCase
import pl.gov.mc.protegosafe.ui.common.BaseViewModel
import timber.log.Timber

class MainViewModel(
    private val saveNotificationDataUseCase: SaveNotificationDataUseCase,
    private val signInUseCase: SignInUseCase
): BaseViewModel() {

    init {
        signInUseCase.execute()
            .subscribe({
                Timber.d("Sign in completed")
            }, {
                Timber.e(it, "Sign in failed")
            })
            .addTo(disposables)
    }

    fun onNotificationDataReceived(data: String) {
        saveNotificationDataUseCase.execute(data)
    }
}