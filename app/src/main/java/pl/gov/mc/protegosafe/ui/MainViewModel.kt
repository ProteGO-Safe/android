package pl.gov.mc.protegosafe.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import io.reactivex.rxkotlin.addTo
import pl.gov.mc.protegosafe.domain.model.SafetyNetResult
import pl.gov.mc.protegosafe.domain.usecase.CheckDeviceRootedUseCase
import pl.gov.mc.protegosafe.domain.usecase.HandleNewUriUseCase
import pl.gov.mc.protegosafe.domain.usecase.SaveRouteUseCase
import pl.gov.mc.protegosafe.ui.common.BaseViewModel
import pl.gov.mc.protegosafe.ui.common.livedata.SingleLiveEvent
import timber.log.Timber

class MainViewModel(
    private val appUpdateManager: AppUpdateManager,
    private val saveRouteUseCase: SaveRouteUseCase,
    private val handleNewUriUseCase: HandleNewUriUseCase,
    private val checkDeviceRootedUseCase: CheckDeviceRootedUseCase
) : BaseViewModel() {

    private val _appUpdateInfoEvent = MutableLiveData<AppUpdateInfo>()
    val appUpdateInfoEvent: LiveData<AppUpdateInfo> = _appUpdateInfoEvent
    private val _showSafetyNetProblem = SingleLiveEvent<Unit>()
    val showSafetyNetProblem: LiveData<Unit> = _showSafetyNetProblem

    init {
        checkDeviceRooted()
        checkForApplicationUpdates()
    }

    fun onRouteReceived(data: String) {
        saveRouteUseCase.execute(data)
            .subscribe(
                {
                    Timber.d("Route saved")
                },
                {
                    Timber.e(it)
                }
            ).addTo(disposables)
    }

    fun handleNewUri(uri: Uri) {
        handleNewUriUseCase.execute(uri.toString())
            .subscribe(
                {
                    Timber.d("Uri handled")
                },
                {
                    Timber.e(it)
                }
            ).addTo(disposables)
    }

    private fun checkDeviceRooted() {
        Timber.d("checkDeviceRooted")
        checkDeviceRootedUseCase.execute()
            .subscribe(
                {
                    if (it == SafetyNetResult.Failure.SafetyError) {
                        _showSafetyNetProblem.postValue(Unit)
                    }
                },
                {
                    Timber.e(it, "SafetyNetError")
                }
            ).addTo(disposables)
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
