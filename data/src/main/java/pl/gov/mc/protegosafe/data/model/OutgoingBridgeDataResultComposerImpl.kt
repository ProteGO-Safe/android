package pl.gov.mc.protegosafe.data.model

import com.google.gson.Gson
import pl.gov.mc.protegosafe.domain.model.AppLifecycleState
import pl.gov.mc.protegosafe.domain.model.ExposureItem
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeysUploadState

class OutgoingBridgeDataResultComposerImpl : OutgoingBridgeDataResultComposer {
    override fun composeTemporaryExposureKeysUploadResult(state: TemporaryExposureKeysUploadState): String =
        Gson().toJson(TemporaryExposureKeysUploadResult(state.code))

    override fun composeAppLifecycleStateResult(state: AppLifecycleState): String =
        Gson().toJson(AppLifecycleStateResult(state.code))

    override fun composeAnalyzeResult(exposure: ExposureItem): String =
        Gson().toJson(AnalyzeResultData(RiskLevelData.fromRiskScore(exposure.riskScore).value))
}
