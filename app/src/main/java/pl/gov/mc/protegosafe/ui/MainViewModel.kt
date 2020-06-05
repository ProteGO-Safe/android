package pl.gov.mc.protegosafe.ui

import androidx.lifecycle.LiveData
import io.reactivex.rxkotlin.addTo
import pl.gov.mc.protegosafe.domain.model.SafetyNetResult
import pl.gov.mc.protegosafe.domain.usecase.CheckDeviceRootedUseCase
import pl.gov.mc.protegosafe.domain.usecase.SaveNotificationDataUseCase
import pl.gov.mc.protegosafe.ui.common.BaseViewModel
import pl.gov.mc.protegosafe.ui.common.livedata.SingleLiveEvent
import timber.log.Timber

class MainViewModel(
    private val saveNotificationDataUseCase: SaveNotificationDataUseCase,
    checkDeviceRootedUseCase: CheckDeviceRootedUseCase
) : BaseViewModel() {

    private val _showSafetyNetProblem = SingleLiveEvent<Unit>()
    val showSafetyNetProblem: LiveData<Unit> = _showSafetyNetProblem

    init {
        checkDeviceRootedUseCase.execute()
            .subscribe({
                if (it == SafetyNetResult.Failure.SafetyError) {
                    _showSafetyNetProblem.postValue(Unit)
                }
            }, {
                Timber.e(it, "SafetyNetError")
            }).addTo(disposables)
    }

    fun onNotificationDataReceived(data: String) {
        saveNotificationDataUseCase.execute(data)
    }
}
