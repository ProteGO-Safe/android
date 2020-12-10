package pl.gov.mc.protegosafe.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.addTo
import pl.gov.mc.protegosafe.data.extension.toJson
import pl.gov.mc.protegosafe.data.mapper.toTemporaryExposureKeyRequestDataList
import pl.gov.mc.protegosafe.domain.model.ExposureNotificationActionNotResolvedException
import pl.gov.mc.protegosafe.domain.model.RiskLevelItem
import pl.gov.mc.protegosafe.helpers.GetTemporaryExposureKeysUseCase
import pl.gov.mc.protegosafe.helpers.GetWebViewLoggingStatusUseCase
import pl.gov.mc.protegosafe.helpers.SetRiskHelperUseCase
import pl.gov.mc.protegosafe.helpers.SetWebViewLoggingEnabledUseCase
import pl.gov.mc.protegosafe.helpers.SetWorkersIntervalUseCase
import pl.gov.mc.protegosafe.ui.common.BaseViewModel
import pl.gov.mc.protegosafe.ui.common.livedata.SingleLiveEvent
import timber.log.Timber

class TestHelpersViewModel(
    private val setRiskHelperUseCase: SetRiskHelperUseCase,
    private val setWorkersIntervalUseCase: SetWorkersIntervalUseCase,
    private val setWebViewLoggingEnabledUseCase: SetWebViewLoggingEnabledUseCase,
    private val getWebViewLoggingStatusUseCase: GetWebViewLoggingStatusUseCase,
    private val getTemporaryExposureKeysUseCase: GetTemporaryExposureKeysUseCase
) : BaseViewModel() {

    val loggingStatus = SingleLiveEvent<Boolean>()
    private val _successfulEvent = MutableLiveData<String>()
    val successfulEvent: LiveData<String> = _successfulEvent
    private val _failedEvent = MutableLiveData<String>()
    val failedEvent: LiveData<String> = _failedEvent
    private val _shareTemporaryExposureKeys = MutableLiveData<String>()
    val shareTemporaryExposureKeys: LiveData<String> = _shareTemporaryExposureKeys
    private val _requestExposureNotificationPermission =
        SingleLiveEvent<ExposureNotificationActionNotResolvedException>()
    val requestResolve: LiveData<ExposureNotificationActionNotResolvedException> =
        _requestExposureNotificationPermission

    init {
        getWebViewLoggingState()
    }

    private fun getWebViewLoggingState() {
        getWebViewLoggingStatusUseCase.execute()
            .subscribe(
                {
                    loggingStatus.postValue(it)
                },
                {
                    Timber.e(it)
                }
            ).addTo(disposables)
    }

    fun onRiskChangeClick(riskLevelItem: RiskLevelItem) {
        setRiskHelperUseCase.execute(riskLevelItem)
            .subscribe(
                {
                    _successfulEvent.postValue(it)
                },
                {
                    _failedEvent.postValue(it.message)
                }
            ).addTo(disposables)
    }

    fun onWorkersIntervalClick(interval: Long) {
        setWorkersIntervalUseCase.execute(interval)
            .subscribe(
                {
                    _successfulEvent.postValue(it)
                },
                {
                    _failedEvent.postValue(it.message)
                }
            ).addTo(disposables)
    }

    fun onUiLogsSwitch(isChecked: Boolean) {
        if (isChecked != loggingStatus.value) {
            setWebViewLoggingEnabledUseCase.execute(isChecked)
                .subscribe(
                    {
                        _successfulEvent.postValue(it)
                    },
                    {
                        _failedEvent.postValue(it.message)
                    }
                ).addTo(disposables)
        }
    }

    fun shareTemporaryExposureKeys() {
        getTemporaryExposureKeysUseCase.execute()
            .subscribe(
                {
                    _shareTemporaryExposureKeys.postValue(it.toTemporaryExposureKeyRequestDataList().toJson())
                },
                { error ->
                    (error as? ExposureNotificationActionNotResolvedException)?.let {
                        _requestExposureNotificationPermission.postValue(it)
                    }
                }
            ).addTo(disposables)
    }
}
