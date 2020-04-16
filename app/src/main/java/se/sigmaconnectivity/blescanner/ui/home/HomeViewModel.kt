package se.sigmaconnectivity.blescanner.ui.home

import io.reactivex.rxkotlin.addTo
import se.sigmaconnectivity.blescanner.domain.model.IncomingBridgeDataItem
import se.sigmaconnectivity.blescanner.domain.model.IncomingBridgeDataType
import se.sigmaconnectivity.blescanner.domain.model.OutgoingBridgeDataType
import se.sigmaconnectivity.blescanner.domain.usecase.OnGetBridgeDataUseCase
import se.sigmaconnectivity.blescanner.domain.usecase.OnSetBridgeDataUseCase
import se.sigmaconnectivity.blescanner.ui.common.BaseViewModel

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
