package pl.gov.mc.protegosafe.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import pl.gov.mc.protegosafe.domain.exception.NoInternetConnectionException
import pl.gov.mc.protegosafe.domain.model.ActionRequiredItem
import pl.gov.mc.protegosafe.domain.model.ActivityResult
import pl.gov.mc.protegosafe.domain.model.AppLifecycleState
import pl.gov.mc.protegosafe.domain.model.IncomingBridgeDataItem
import pl.gov.mc.protegosafe.domain.model.IncomingBridgeDataType
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataType
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeysUploadState
import pl.gov.mc.protegosafe.domain.model.ExposureNotificationActionNotResolvedException
import pl.gov.mc.protegosafe.domain.usecase.ComposeAppLifecycleStateBrideDataUseCase
import pl.gov.mc.protegosafe.domain.usecase.GetServicesStatusUseCase
import pl.gov.mc.protegosafe.domain.usecase.OnGetBridgeDataUseCase
import pl.gov.mc.protegosafe.domain.usecase.OnSetBridgeDataUseCase
import pl.gov.mc.protegosafe.domain.usecase.ProcessPendingActivityResultUseCase
import pl.gov.mc.protegosafe.domain.usecase.StartExposureNotificationUseCase
import pl.gov.mc.protegosafe.domain.usecase.StorePendingActivityResultUseCase
import pl.gov.mc.protegosafe.domain.usecase.UploadTemporaryExposureKeysWithCachedPayloadUseCase
import pl.gov.mc.protegosafe.logging.webViewTimber
import pl.gov.mc.protegosafe.ui.common.BaseViewModel
import pl.gov.mc.protegosafe.ui.common.livedata.SingleLiveEvent
import timber.log.Timber

class HomeViewModel(
    private val onSetBridgeDataUseCase: OnSetBridgeDataUseCase,
    private val servicesStatusUseCase: GetServicesStatusUseCase,
    private val onGetBridgeDataUseCase: OnGetBridgeDataUseCase,
    private val startExposureNotificationUseCase: StartExposureNotificationUseCase,
    private val composeAppLifecycleStateBrideDataUseCase: ComposeAppLifecycleStateBrideDataUseCase,
    private val uploadTemporaryExposureKeysWithCachedPayloadUseCase: UploadTemporaryExposureKeysWithCachedPayloadUseCase,
    private val storePendingActivityResultUseCase: StorePendingActivityResultUseCase,
    private val processPendingActivityResultUseCase: ProcessPendingActivityResultUseCase,
    private val outgoingBridgeDataResultComposer: OutgoingBridgeDataResultComposer
) : BaseViewModel() {

    private val _javascriptCode = MutableLiveData<String>()
    val javascriptCode: LiveData<String> = _javascriptCode

    private val _requestBluetooth = SingleLiveEvent<Unit>()
    val requestBluetooth: LiveData<Unit> = _requestBluetooth

    private val _requestLocation = SingleLiveEvent<Unit>()
    val requestLocation: LiveData<Unit> = _requestLocation

    private val _requestNotifications = SingleLiveEvent<Unit>()
    val requestNotifications: LiveData<Unit> = _requestNotifications

    private val _requestClearData = SingleLiveEvent<Unit>()
    val requestClearData: LiveData<Unit> = _requestClearData

    private val _showConnectionError = MutableLiveData<Unit>()
    val showConnectionError: LiveData<Unit> = _showConnectionError

    private val _requestExposureNotificationPermission =
        SingleLiveEvent<ExposureNotificationActionNotResolvedException>()
    val requestResolve: LiveData<ExposureNotificationActionNotResolvedException> =
        _requestExposureNotificationPermission

    fun setBridgeData(dataType: Int, dataJson: String) {
        onSetBridgeDataUseCase.execute(
            IncomingBridgeDataItem(
                type = IncomingBridgeDataType.valueOf(dataType),
                payload = dataJson
            ), ::onResultActionRequired
        ).subscribeBy(
            onComplete = { Timber.d("OnSetBridgeData executed") },
            onError = { error -> handleError(error) }
        ).addTo(disposables)
    }

    fun onActivityResult(activityResult: ActivityResult) {
        storePendingActivityResultUseCase.execute(activityResult)
            .subscribe({
                Timber.d("Storing pending activity result finished")
            }, {
                Timber.e(it, "Storing pending activity result  failed")
            }).addTo(disposables)
    }

    fun getBridgeData(dataType: Int, data: String, requestId: String) {
        onGetBridgeDataUseCase.execute(OutgoingBridgeDataType.valueOf(dataType))
            .subscribe({
                webViewTimber().d("getBridgeData: $dataType output: $it")
                bridgeDataResponse(it, dataType, requestId)
            }, {
                Timber.e(it, "getBridgeData failed")
            }).addTo(disposables)
    }

    fun onUploadRetry() {
        uploadTemporaryExposureKeysWithCachedPayload()
    }

    fun sendUploadCanceled() {
        onBridgeData(
            OutgoingBridgeDataType.TEMPORARY_EXPOSURE_KEYS_UPLOAD_STATUS.code,
            outgoingBridgeDataResultComposer.composeTemporaryExposureKeysUploadResult(
                TemporaryExposureKeysUploadState.OTHER
            )
        )
    }

    private fun handleError(error: Throwable) {
        when (error) {
            is ExposureNotificationActionNotResolvedException -> {
                handleNotResolvedException(error)
            }
            is NoInternetConnectionException -> {
                _showConnectionError.postValue(Unit)
            }
            else -> {
                Timber.e(error, "Problem can not be handled")
            }
        }
    }

    private fun onTemporaryExposureKeysAccessGranted() {
        uploadTemporaryExposureKeysWithCachedPayload()
    }

    private fun uploadTemporaryExposureKeysWithCachedPayload() {
        uploadTemporaryExposureKeysWithCachedPayloadUseCase.execute(::onResultActionRequired)
            .subscribe({
                Timber.d("Temporary exposure keys upload with cached payload finished")
            }, {
                handleError(it)
            }).addTo(disposables)
    }

    private fun onExposureNotificationPermissionGranted() {
        startExposureNotificationUseCase.execute()
            .subscribe({
                Timber.d("Exposure Notification started")
                onResultActionRequired(ActionRequiredItem.SendServicesStatus)
            }, {
                Timber.d(it, "Starting Exposure Notification failed")
            }).addTo(disposables)
    }

    fun processPendingActivityResult() {
        processPendingActivityResultUseCase.execute(::onResultActionRequired)
            .subscribe({
                Timber.d("processing pending activity result success")
            }, {
                Timber.e(it, "processing pending activity result failed")
            }).addTo(disposables)
    }

    private fun onResultActionRequired(actionRequired: ActionRequiredItem) {
        when (actionRequired) {
            is ActionRequiredItem.SendServicesStatus -> {
                sendServicesStatus()
            }
            is ActionRequiredItem.RequestEnableBluetooth -> {
                _requestBluetooth.postValue(Unit)
            }
            is ActionRequiredItem.RequestEnableLocation -> {
                _requestLocation.postValue(Unit)
            }
            is ActionRequiredItem.RequestEnableNotifications -> {
                _requestNotifications.postValue(Unit)
            }
            is ActionRequiredItem.ClearExposureNotificationData -> {
                _requestClearData.postValue(Unit)
            }
            is ActionRequiredItem.SendTemporaryExposureKeysUploadResult -> {
                onBridgeData(actionRequired.dataType, actionRequired.dataJson)
            }
            is ActionRequiredItem.SendTemporaryExposureKeysUploadFailure -> {
                onBridgeData(
                    OutgoingBridgeDataType.TEMPORARY_EXPOSURE_KEYS_UPLOAD_STATUS.code,
                    outgoingBridgeDataResultComposer.composeTemporaryExposureKeysUploadResult(
                        TemporaryExposureKeysUploadState.FAILURE
                    )
                )
            }
            is ActionRequiredItem.ExposureNotificationPermissionGranted -> {
                onExposureNotificationPermissionGranted()
            }
            is ActionRequiredItem.TemporaryExposureKeysPermissionGranted -> {
                onTemporaryExposureKeysAccessGranted()
            }
        }
    }

    fun onAppLifecycleStateChanged(state: AppLifecycleState) {
        Timber.d("onAppLifecycleStateChanged $state")
        if (state == AppLifecycleState.RESUMED) {
            sendServicesStatus()
        }
        composeAppLifecycleStateBrideDataUseCase.execute(state)
            .subscribe({
                onBridgeData(OutgoingBridgeDataType.APP_LIFECYCLE_STATE.code, it)
            }, {
                Timber.e(it, "onAppLifecycleStateChanged failed")
            }).addTo(disposables)
    }

    private fun sendServicesStatus() {
        Timber.d("onBluetoothEnable")
        servicesStatusUseCase.execute()
            .subscribe({
                onBridgeData(OutgoingBridgeDataType.SERVICES_STATUS.code, it)
            }, {
                Timber.e(it, "sendServicesStatus failed")
            }).addTo(disposables)
    }

    private fun bridgeDataResponse(body: String, dataType: Int, requestId: String) {
        val codeToExecute = "bridgeDataResponse('$body', $dataType, '$requestId')"
        webViewTimber().d("run Javascript: -$codeToExecute-")
        _javascriptCode.postValue(codeToExecute)
    }

    private fun onBridgeData(dataType: Int, dataJson: String) {
        val codeToExecute = "onBridgeData($dataType, '$dataJson')"
        webViewTimber().d("run Javascript: -$codeToExecute-")
        _javascriptCode.postValue(codeToExecute)
    }

    private fun handleNotResolvedException(exception: ExposureNotificationActionNotResolvedException) {
        _requestExposureNotificationPermission.postValue(exception)
    }
}
