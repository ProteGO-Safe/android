package pl.gov.mc.protegosafe.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import io.reactivex.rxkotlin.addTo
import pl.gov.mc.protegosafe.domain.model.SafetyNetResult
import pl.gov.mc.protegosafe.domain.usecase.CheckDeviceRootedUseCase
import pl.gov.mc.protegosafe.domain.usecase.SaveNotificationDataUseCase
import pl.gov.mc.protegosafe.ui.common.BaseViewModel
import pl.gov.mc.protegosafe.ui.common.livedata.SingleLiveEvent
import timber.log.Timber

class MainViewModel(
    private val appUpdateManager: AppUpdateManager,
    private val saveNotificationDataUseCase: SaveNotificationDataUseCase,
    checkDeviceRootedUseCase: CheckDeviceRootedUseCase
) : BaseViewModel() {

    init {
        checkDeviceRootedUseCase.execute()
            .subscribe({
                if (it == SafetyNetResult.Failure.SafetyError) {
                    _showSafetyNetProblem.postValue(Unit)
                }
            }, {
                Timber.e(it, "SafetyNetError")
            }).addTo(disposables)

        checkForApplicationUpdates()
    }

    private val _appUpdateInfoEvent = MutableLiveData<AppUpdateInfo>()
    val appUpdateInfoEvent: LiveData<AppUpdateInfo> = _appUpdateInfoEvent
    private val _showSafetyNetProblem = SingleLiveEvent<Unit>()
    val showSafetyNetProblem: LiveData<Unit> = _showSafetyNetProblem

    fun onNotificationDataReceived(data: String) {
        saveNotificationDataUseCase.execute(data)
    }

    private fun checkForApplicationUpdates() {
        Timber.d("checkForApplicationUpdates")
        appUpdateManager.appUpdateInfo.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _appUpdateInfoEvent.postValue(task.result)
            } else {
                Timber.e("Error while checking application updates: ${task.exception}")
            }
        }
    }
}
