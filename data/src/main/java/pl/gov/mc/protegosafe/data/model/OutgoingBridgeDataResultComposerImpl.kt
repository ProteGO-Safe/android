package pl.gov.mc.protegosafe.data.model

import pl.gov.mc.protegosafe.data.extension.toJson
import pl.gov.mc.protegosafe.domain.model.AppLifecycleState
import pl.gov.mc.protegosafe.domain.model.ExposureItem
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.model.RiskLevelConfigurationItem
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeysUploadState

class OutgoingBridgeDataResultComposerImpl : OutgoingBridgeDataResultComposer {
    override fun composeTemporaryExposureKeysUploadResult(state: TemporaryExposureKeysUploadState): String =
        TemporaryExposureKeysUploadResult(state.code).toJson()

    override fun composeAppLifecycleStateResult(state: AppLifecycleState): String =
        AppLifecycleStateResult(state.code).toJson()

    override fun composeAnalyzeResult(
        riskLevelConfigurationItem: RiskLevelConfigurationItem,
        exposure: ExposureItem
    ): String = AnalyzeResultData(
        RiskLevelData.fromRiskScore(
            riskLevelConfigurationItem,
            exposure.riskScore
        ).value
    ).toJson()

    override fun composeAppVersionNameResult(versionName: String): String {
        return AppVersionNameResult(versionName).toJson()
    }

    override fun composeSystemLanguageResult(languageISO: String): String {
        return SystemLanguageResult(languageISO).toJson()
    }
}
