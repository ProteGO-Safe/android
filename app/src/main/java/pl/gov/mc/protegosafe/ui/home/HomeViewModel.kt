package pl.gov.mc.protegosafe.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import org.json.JSONObject
import pl.gov.mc.protegosafe.domain.model.IncomingBridgeDataItem
import pl.gov.mc.protegosafe.domain.model.IncomingBridgeDataType
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataType
import pl.gov.mc.protegosafe.domain.usecase.GetServicesStatusUseCase
import pl.gov.mc.protegosafe.domain.usecase.OnGetBridgeDataUseCase
import pl.gov.mc.protegosafe.domain.usecase.OnSetBridgeDataUseCase
import pl.gov.mc.protegosafe.ui.common.BaseViewModel
import pl.gov.mc.protegosafe.ui.common.livedata.SingleLiveEvent
import timber.log.Timber
import java.util.concurrent.TimeUnit

class HomeViewModel(
    private val onSetBridgeDataUseCase: OnSetBridgeDataUseCase,
    private val onGetBridgeDataUseCase: OnGetBridgeDataUseCase,
    private val servicesStatusUseCase: GetServicesStatusUseCase
) : BaseViewModel() {

    private val _javascriptCode = MutableLiveData<String>()
    val javascriptCode: LiveData<String> = _javascriptCode

    private val _requestPermissions = SingleLiveEvent<Unit>()
    val requestPermissions: LiveData<Unit> = _requestPermissions

    private val _requestBluetooth = SingleLiveEvent<Unit>()
    val requestBluetooth: LiveData<Unit> = _requestBluetooth

    init{
        Observable.interval(10, 15, TimeUnit.SECONDS)
            .take(1)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Timber.d("WNASILOWSKILOG sub")
            setBridgeData(IncomingBridgeDataType.REQUEST_BLUETOOTH.code, "")
        }, { Timber.e(it, "WNASILOWSKILOG")})

    }

    fun setBridgeData(dataType: Int, dataJson: String) {
        when(IncomingBridgeDataType.valueOf(dataType)) {
            IncomingBridgeDataType.REQUEST_PERMISSION -> {
                _requestPermissions.setValue(Unit)
            }
            IncomingBridgeDataType.REQUEST_BLUETOOTH -> {
                _requestBluetooth.setValue(Unit)
            }
            else -> {
                onSetBridgeDataUseCase.execute(
                    IncomingBridgeDataItem(
                        type = IncomingBridgeDataType.valueOf(dataType),
                        payload = dataJson
                    )
                ).subscribe().addTo(disposables)
            }
        }
    }

    fun getBridgeData(dataType: Int): String {
        return onGetBridgeDataUseCase.execute(OutgoingBridgeDataType.valueOf(dataType))
    }

    fun onPermissionsAccepted() {
        val servicesStatus = servicesStatusUseCase.execute()
        Timber.d("onPermissionsAccepted")
        onBridgeData(OutgoingBridgeDataType.PERMISSIONS_ACCEPTED.code, servicesStatus)
    }

    fun onBluetoothEnabled() {
        val servicesStatus = servicesStatusUseCase.execute()
        Timber.d("onPermissionsAccepted")
        onBridgeData(OutgoingBridgeDataType.BLUETOOTH_ENABLED.code, servicesStatus)
    }

    private fun onBridgeData(dataType: Int, dataJson: String) {
        val escapedJson = JSONObject.quote(dataJson)
        val codeToExecute = """
            alert('onBridgeData - ${dataType}');
            onBridgeData($dataType, $escapedJson);
        """.trimIndent()
        Timber.d("run Javascript: -$codeToExecute-")
        _javascriptCode.value = codeToExecute
    }
}
