package pl.gov.mc.protegosafe.ui.home

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.exception.CovidTestNotCompatibleDeviceException
import pl.gov.mc.protegosafe.domain.exception.NoInternetConnectionException
import pl.gov.mc.protegosafe.domain.exception.UploadException
import pl.gov.mc.protegosafe.domain.model.ActionRequiredItem
import pl.gov.mc.protegosafe.domain.model.ActivityResult
import pl.gov.mc.protegosafe.domain.model.AppLifecycleState
import pl.gov.mc.protegosafe.domain.model.ExposureNotificationActionNotResolvedException
import pl.gov.mc.protegosafe.domain.model.GetBridgeDataUIRequestItem
import pl.gov.mc.protegosafe.domain.model.IncomingBridgeDataItem
import pl.gov.mc.protegosafe.domain.model.IncomingBridgeDataType
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataType
import pl.gov.mc.protegosafe.domain.model.SendSmsItem
import pl.gov.mc.protegosafe.domain.model.SetBridgeDataUIRequestItem
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeysUploadState
import pl.gov.mc.protegosafe.domain.repository.UiRequestCacheRepository
import pl.gov.mc.protegosafe.domain.usecase.ComposeAppLifecycleStateBrideDataUseCase
import pl.gov.mc.protegosafe.domain.usecase.GetRouteDataAndClearUseCase
import pl.gov.mc.protegosafe.domain.usecase.GetServicesStatusUseCase
import pl.gov.mc.protegosafe.domain.usecase.OnGetBridgeDataUseCase
import pl.gov.mc.protegosafe.domain.usecase.OnSetBridgeDataUseCase
import pl.gov.mc.protegosafe.domain.usecase.ProcessPendingActivityResultUseCase
import pl.gov.mc.protegosafe.domain.usecase.StartExposureNotificationUseCase
import pl.gov.mc.protegosafe.domain.usecase.StorePendingActivityResultUseCase
import pl.gov.mc.protegosafe.domain.usecase.UploadTemporaryExposureKeysWithCachedPayloadUseCase
import pl.gov.mc.protegosafe.domain.usecase.covidtest.UpdateTestSubscriptionStatusUseCase
import pl.gov.mc.protegosafe.logging.WebViewTimber
import pl.gov.mc.protegosafe.ui.common.BaseViewModel
import pl.gov.mc.protegosafe.ui.common.livedata.SingleLiveEvent
import timber.log.Timber
import java.net.UnknownHostException

class HomeViewModel(
    private val onSetBridgeDataUseCase: OnSetBridgeDataUseCase,
    private val servicesStatusUseCase: GetServicesStatusUseCase,
    private val onGetBridgeDataUseCase: OnGetBridgeDataUseCase,
    private val startExposureNotificationUseCase: StartExposureNotificationUseCase,
    private val composeAppLifecycleStateBrideDataUseCase: ComposeAppLifecycleStateBrideDataUseCase,
    private val uploadTemporaryExposureKeysWithCachedPayloadUseCase: UploadTemporaryExposureKeysWithCachedPayloadUseCase,
    private val storePendingActivityResultUseCase: StorePendingActivityResultUseCase,
    private val processPendingActivityResultUseCase: ProcessPendingActivityResultUseCase,
    private val updateTestSubscriptionStatusUseCase: UpdateTestSubscriptionStatusUseCase,
    private val outgoingBridgeDataResultComposer: OutgoingBridgeDataResultComposer,
    private val getRouteAndClearUseCase: GetRouteDataAndClearUseCase,
    private val uiRequestCacheRepository: UiRequestCacheRepository
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

    private val _restartActivity = SingleLiveEvent<Unit>()
    val restartActivity: LiveData<Unit> = _restartActivity

    private val _closeApplication = SingleLiveEvent<Unit>()
    val closeApplication: LiveData<Unit> = _closeApplication

    private val _showUploadError = SingleLiveEvent<Exception>()
    val showConnectionError: LiveData<Exception> = _showUploadError

    private val _requestAppReview = SingleLiveEvent<Unit>()
    val requestAppReview: LiveData<Unit> = _requestAppReview

    private val _openSmsApp = SingleLiveEvent<SendSmsItem>()
    val openSmsApp: LiveData<SendSmsItem> = _openSmsApp

    private val _requestExposureNotificationPermission =
        SingleLiveEvent<ExposureNotificationActionNotResolvedException>()
    val requestResolve: LiveData<ExposureNotificationActionNotResolvedException> =
        _requestExposureNotificationPermission

    fun setBridgeData(dataType: Int, dataJson: String) {
        onSetBridgeDataUseCase.execute(
            IncomingBridgeDataItem(
                type = IncomingBridgeDataType.valueOf(dataType),
                payload = dataJson
            ),
            ::onResultActionRequired
        ).subscribeBy(
            onComplete = { Timber.d("OnSetBridgeData executed") },
            onError = { error -> handleError(error) }
        ).addTo(disposables)
    }

    fun onActivityResult(activityResult: ActivityResult) {
        storePendingActivityResultUseCase.execute(activityResult)
            .subscribe(
                {
                    Timber.d("Storing pending activity result finished")
                },
                {
                    Timber.e(it, "Storing pending activity result  failed")
                }
            ).addTo(disposables)
    }

    fun getBridgeData(dataType: Int, data: String, requestId: String) {
        onGetBridgeDataUseCase.execute(
            OutgoingBridgeDataType.valueOf(dataType), data, requestId, ::onResultActionRequired
        )
            .subscribe(
                {
                    WebViewTimber.d("getBridgeData: $dataType output: $it")
                    bridgeDataResponse(it, dataType, requestId)
                },
                {
                    handleError(it)
                    Timber.e(it, "getBridgeData failed")
                }
            ).addTo(disposables)
    }

    fun onRequestRetry() {
        uiRequestCacheRepository.getCachedRequest()?.let { uiRequest ->
            when (uiRequest) {
                is SetBridgeDataUIRequestItem -> {
                    retryCachedSetBridgeDataRequest(uiRequest)
                }
                is GetBridgeDataUIRequestItem -> {
                    uiRequestCacheRepository.retryCachedRequest(uiRequest, ::getBridgeData)
                }
            }
        }
    }

    fun onRequestCanceled() {
        uiRequestCacheRepository.getCachedRequest()?.let { uiRequest ->
            when (uiRequest) {
                is SetBridgeDataUIRequestItem -> {
                    uiRequestCacheRepository.cancelCachedRequest(uiRequest, ::onBridgeData)
                }
                is GetBridgeDataUIRequestItem -> {
                    uiRequestCacheRepository.cancelCachedRequest(uiRequest, ::bridgeDataResponse)
                }
            }
        }
    }

    fun onBackButtonPressed() {
        onBridgeData(
            OutgoingBridgeDataType.BACK_BUTTON_PRESSED.code,
            outgoingBridgeDataResultComposer.composeBackButtonPressedResult()
        )
    }

    private fun sendAccessDeniedUploadStatus() {
        onBridgeData(
            OutgoingBridgeDataType.TEMPORARY_EXPOSURE_KEYS_UPLOAD_STATUS.code,
            outgoingBridgeDataResultComposer.composeTemporaryExposureKeysUploadResult(
                TemporaryExposureKeysUploadState.ACCESS_DENIED
            )
        )
    }

    private fun handleError(error: Throwable) {
        when (error) {
            is ExposureNotificationActionNotResolvedException -> {
                handleNotResolvedException(error)
            }
            is UploadException.PinVerificationFailed -> {
                Timber.e(error, "No need to handle error")
            }
            is NoInternetConnectionException,
            is UnknownHostException,
            is UploadException -> {
                _showUploadError.postValue(error as Exception)
            }
            is CovidTestNotCompatibleDeviceException -> {
                onRequestCanceled()
                _showUploadError.postValue(error as Exception)
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
            .subscribe(
                {
                    Timber.d("Temporary exposure keys upload with cached payload finished")
                },
                {
                    handleError(it)
                }
            ).addTo(disposables)
    }

    private fun onExposureNotificationPermissionGranted() {
        startExposureNotificationUseCase.execute()
            .subscribe(
                {
                    Timber.d("Exposure Notification started")
                    onResultActionRequired(ActionRequiredItem.SendServicesStatus)
                },
                {
                    Timber.d(it, "Starting Exposure Notification failed")
                }
            ).addTo(disposables)
    }

    fun processPendingActivityResult() {
        processPendingActivityResultUseCase.execute(::onResultActionRequired)
            .subscribe(
                {
                    Timber.d("processing pending activity result success")
                },
                {
                    Timber.e(it, "processing pending activity result failed")
                }
            ).addTo(disposables)
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
            is ActionRequiredItem.ClearData -> {
                _requestClearData.postValue(Unit)
            }
            is ActionRequiredItem.SendTemporaryExposureKeysUploadResult -> {
                onBridgeData(actionRequired.dataType, actionRequired.dataJson)
            }
            is ActionRequiredItem.TemporaryExposureKeysPermissionDenied -> {
                sendAccessDeniedUploadStatus()
            }
            is ActionRequiredItem.ExposureNotificationPermissionGranted -> {
                onExposureNotificationPermissionGranted()
            }
            is ActionRequiredItem.TemporaryExposureKeysPermissionGranted -> {
                onTemporaryExposureKeysAccessGranted()
            }
            is ActionRequiredItem.RestartActivity -> {
                _restartActivity.postValue(Unit)
            }
            is ActionRequiredItem.CloseApp -> {
                _closeApplication.postValue(Unit)
            }
            is ActionRequiredItem.UpdateTestSubscription -> {
                updateTestSubscriptionStatus()
            }
            is ActionRequiredItem.AppReview -> {
                _requestAppReview.postValue(Unit)
            }
            is ActionRequiredItem.OpenSmsApp -> {
                val item = SendSmsItem(number = actionRequired.number, text = actionRequired.text)
                _openSmsApp.postValue(item)
            }
        }
    }

    fun onAppLifecycleStateChanged(state: AppLifecycleState, webViewProgress: Int? = null) {
        Timber.d("onAppLifecycleStateChanged $state")
        if (state == AppLifecycleState.RESUMED) {
            sendServicesStatus()
        }
        composeAppLifecycleStateBrideDataUseCase.execute(state)
            .subscribe(
                {
                    onBridgeData(OutgoingBridgeDataType.APP_LIFECYCLE_STATE.code, it)
                },
                {
                    Timber.e(it, "onAppLifecycleStateChanged failed")
                }
            ).addTo(disposables)

        if (webViewProgress == MAX_WEBVIEW_PROGRESS) {
            sendRerouteRequestIfNotEmpty()
        }
    }

    fun onPageFinished() {
        sendRerouteRequestIfNotEmpty()
    }

    private fun sendRerouteRequestIfNotEmpty() {
        getRouteAndClearUseCase.execute()
            .subscribe(
                {
                    if (it.isNotEmpty()) {
                        onBridgeData(OutgoingBridgeDataType.REROUTE_USER.code, it)
                    }
                },
                {
                    Timber.e(it, "Can not reroute user")
                }
            ).addTo(disposables)
    }

    private fun sendServicesStatus() {
        Timber.d("onBluetoothEnable")
        servicesStatusUseCase.execute()
            .subscribe(
                {
                    onBridgeData(OutgoingBridgeDataType.SERVICES_STATUS.code, it)
                },
                {
                    Timber.e(it, "sendServicesStatus failed")
                }
            ).addTo(disposables)
    }

    private fun updateTestSubscriptionStatus() {
        Timber.d("updateTestSubscriptionStatus")
        updateTestSubscriptionStatusUseCase.execute()
            .subscribe(
                {
                    onBridgeData(OutgoingBridgeDataType.GET_COVID_TEST_SUBSCRIPTION_STATUS.code, it)
                },
                {
                    Timber.e(it, "sendServicesStatus failed")
                }
            ).addTo(disposables)
    }

    private fun retryCachedSetBridgeDataRequest(
        setBridgeDataUIRequestItem: SetBridgeDataUIRequestItem
    ) {
        uiRequestCacheRepository.retryCachedRequest(
            setBridgeDataUIRequestItem,
            ::onResultActionRequired
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Timber.d("Success")
                },
                {
                    handleError(it)
                    Timber.e(it)
                }
            ).addTo(disposables)
        // TODO move to usecase
    }

    private fun bridgeDataResponse(body: String, dataType: Int, requestId: String) {
        runJavaScriptCode("bridgeDataResponse('$body', $dataType, '$requestId')")
    }

    private fun onBridgeData(dataType: Int, dataJson: String) {
        runJavaScriptCode("onBridgeData($dataType, '$dataJson')")
    }

    @MainThread
    private fun runJavaScriptCode(codeToExecute: String) {
        WebViewTimber.d("run Javascript: -$codeToExecute-")
        _javascriptCode.value = codeToExecute
    }

    private fun handleNotResolvedException(exception: ExposureNotificationActionNotResolvedException) {
        _requestExposureNotificationPermission.postValue(exception)
    }
}

private const val MAX_WEBVIEW_PROGRESS = 100
