package pl.gov.mc.protegosafe.data.model

import pl.gov.mc.protegosafe.data.extension.toJson
import pl.gov.mc.protegosafe.data.mapper.toDistrictDto
import pl.gov.mc.protegosafe.data.mapper.toVoivodeshipData
import pl.gov.mc.protegosafe.domain.model.AppLifecycleState
import pl.gov.mc.protegosafe.domain.model.DistrictItem
import pl.gov.mc.protegosafe.domain.model.ExposureItem
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.model.RiskLevelConfigurationItem
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeysUploadState
import pl.gov.mc.protegosafe.domain.model.VoivodeshipItem

class OutgoingBridgeDataResultComposerImpl : OutgoingBridgeDataResultComposer {

    enum class Result(val value: Int) {
        SUCCESS(1),
        FAILED(2)
    }

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

    override fun composeDistrictsRestrictionsResult(
        voivodeships: List<VoivodeshipItem>,
        updated: Long
    ): String {
        return DistrictsRestrictionsResult(
            if (voivodeships.isEmpty()) {
                Result.FAILED
            } else {
                Result.SUCCESS
            }.value,
            updated,
            voivodeships.map { it.toVoivodeshipData() }
        ).toJson()
    }

    override fun composeSubscribedDistrictsResult(subscribedDistricts: List<DistrictItem>): String {
        return SubscribedDistrictsResult(subscribedDistricts.map { it.toDistrictDto() }).toJson()
    }
}
