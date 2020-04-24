package pl.gov.mc.protegosafe.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.addTo
import pl.gov.mc.protegosafe.domain.model.IncomingBridgeDataItem
import pl.gov.mc.protegosafe.domain.model.IncomingBridgeDataType
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataType
import pl.gov.mc.protegosafe.domain.usecase.*
import pl.gov.mc.protegosafe.ui.common.BaseViewModel
import pl.gov.mc.protegosafe.ui.common.livedata.SingleLiveEvent
import timber.log.Timber

class HomeViewModel(
    private val onSetBridgeDataUseCase: OnSetBridgeDataUseCase,
    private val servicesStatusUseCase: GetServicesStatusUseCase,
    private val onGetBridgeDataUseCase: OnGetBridgeDataUseCase,
    private val internetConnectionStatusUseCase: GetInternetConnectionStatusUseCase,
    private val enableBTServiceUseCase: EnableBTServiceUseCase
) : BaseViewModel() {

    private val _javascriptCode = MutableLiveData<String>()
    val javascriptCode: LiveData<String> = _javascriptCode

    private val _requestPermissions = SingleLiveEvent<Unit>()
    val requestPermissions: LiveData<Unit> = _requestPermissions

    private val _requestBluetooth = SingleLiveEvent<Unit>()
    val requestBluetooth: LiveData<Unit> = _requestBluetooth

    private val _changeBatteryOptimization = SingleLiveEvent<Unit>()
    val changeBatteryOptimization: LiveData<Unit> = _changeBatteryOptimization

    //TODO: extract logic not directly related to view to outside Classes/functions
    fun setBridgeData(dataType: Int, dataJson: String) {
        when (IncomingBridgeDataType.valueOf(dataType)) {
            IncomingBridgeDataType.REQUEST_PERMISSION -> {
                _requestPermissions.postValue(Unit)
            }
            IncomingBridgeDataType.REQUEST_BLUETOOTH -> {
                _requestBluetooth.postValue(Unit)
            }
            IncomingBridgeDataType.REQUEST_CHANGE_BATTERY_OPTIMIZATION -> {
                _changeBatteryOptimization.postValue(Unit)
            }
            else -> {
                onSetBridgeDataUseCase.execute(
                    IncomingBridgeDataItem(
                        type = IncomingBridgeDataType.valueOf(dataType),
                        payload = dataJson
                    ), ::onBridgeData
                ).subscribe {
                    Timber.d("OnSetBridgeData executed")
                }.addTo(disposables)
            }
        }
    }

    fun getBridgeData(dataType: Int): String {
        return onGetBridgeDataUseCase.execute(OutgoingBridgeDataType.valueOf(dataType))
    }

    fun onPermissionsAccepted() {
        Timber.d("onPermissionsAccepted")
        onBridgeData(
            OutgoingBridgeDataType.PERMISSIONS_ACCEPTED.code,
            servicesStatusUseCase.execute()
        )
        //TODO [PSAFE-416] redesign UX tracking agreement during setup
        enableBTServiceUseCase.execute(true).subscribe()
    }

    fun onBluetoothEnable() {
        Timber.d("onBluetoothEnable")
        onBridgeData(OutgoingBridgeDataType.BLUETOOTH_ENABLED.code, servicesStatusUseCase.execute())
    }

    fun onPowerSettingsResult() {
        Timber.d("onPowerSettingsResult")
        onBridgeData(
            OutgoingBridgeDataType.BATTERY_OPTIMIZATION_SET.code,
            servicesStatusUseCase.execute()
        )
    }

    private fun onBridgeData(dataType: Int, dataJson: String) {
        val codeToExecute = "onBridgeData($dataType, '$dataJson')"
        Timber.d("run Javascript: -$codeToExecute-")
        _javascriptCode.postValue(codeToExecute)
    }

    fun isInternetConnectionAvailable() = internetConnectionStatusUseCase.execute().isConnected()
}
