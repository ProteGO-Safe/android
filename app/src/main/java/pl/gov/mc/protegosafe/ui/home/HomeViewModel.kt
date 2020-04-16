package pl.gov.mc.protegosafe.ui.home

import io.reactivex.rxkotlin.addTo
import pl.gov.mc.protegosafe.domain.model.IncomingBridgeDataItem
import pl.gov.mc.protegosafe.domain.model.IncomingBridgeDataType
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataType
import pl.gov.mc.protegosafe.domain.usecase.OnGetBridgeDataUseCase
import pl.gov.mc.protegosafe.domain.usecase.OnSetBridgeDataUseCase
import pl.gov.mc.protegosafe.ui.common.BaseViewModel

class HomeViewModel(
    private val onSetBridgeDataUseCase: OnSetBridgeDataUseCase,
    private val onGetBridgeDataUseCase: OnGetBridgeDataUseCase
) : BaseViewModel() {

    fun setBridgeData(dataType: Int, dataJson: String) {
        onSetBridgeDataUseCase.execute(
            IncomingBridgeDataItem(
                type = IncomingBridgeDataType.valueOf(dataType),
                payload = dataJson
            )
        ).subscribe().addTo(disposables)
    }

    fun getBridgeData(dataType: Int): String {
        return onGetBridgeDataUseCase.execute(OutgoingBridgeDataType.valueOf(dataType))
    }
}
