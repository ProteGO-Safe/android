package pl.gov.mc.protegosafe.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.addTo
import pl.gov.mc.protegosafe.domain.model.RiskLevelItem
import pl.gov.mc.protegosafe.helpers.SetRiskHelperUseCase
import pl.gov.mc.protegosafe.ui.common.BaseViewModel

class TestHelpersViewModel(
    private val setRiskHelperUseCase: SetRiskHelperUseCase
) : BaseViewModel() {

    private val _successfulEvent = MutableLiveData<String>()
    val successfulEvent: LiveData<String> = _successfulEvent
    private val _failedEvent = MutableLiveData<String>()
    val failedEvent: LiveData<String> = _failedEvent

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
}
