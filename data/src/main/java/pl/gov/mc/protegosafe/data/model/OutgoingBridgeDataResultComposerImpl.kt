package pl.gov.mc.protegosafe.data.model

import com.google.gson.Gson
import pl.gov.mc.protegosafe.data.extension.toJson
import pl.gov.mc.protegosafe.data.mapper.toActivitiesResultData
import pl.gov.mc.protegosafe.data.mapper.toDistrictData
import pl.gov.mc.protegosafe.data.mapper.toENStatsData
import pl.gov.mc.protegosafe.data.mapper.toRiskLevelData
import pl.gov.mc.protegosafe.data.mapper.toTestSubscriptionStatusData
import pl.gov.mc.protegosafe.data.mapper.toVoivodeshipData
import pl.gov.mc.protegosafe.data.model.covidtest.TestSubscriptionStatusResult
import pl.gov.mc.protegosafe.domain.model.ActivitiesResultItem
import pl.gov.mc.protegosafe.domain.model.AppLifecycleState
import pl.gov.mc.protegosafe.domain.model.DistrictItem
import pl.gov.mc.protegosafe.domain.model.ENStatsItem
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.model.ResultStatus
import pl.gov.mc.protegosafe.domain.model.RiskLevelItem
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeysUploadState
import pl.gov.mc.protegosafe.domain.model.TestSubscriptionItem
import pl.gov.mc.protegosafe.domain.model.VoivodeshipItem
import pl.gov.mc.protegosafe.domain.model.VoivodeshipsItem

class OutgoingBridgeDataResultComposerImpl(
    private val gson: Gson
) : OutgoingBridgeDataResultComposer {

    override fun composeTemporaryExposureKeysUploadResult(state: TemporaryExposureKeysUploadState): String =
        SimpleResult(state.code).toJson()

    override fun composeAppLifecycleStateResult(state: AppLifecycleState): String =
        AppLifecycleStateResult(state.code).toJson()

    override fun composeAnalyzeResult(
        riskLevelItem: RiskLevelItem
    ): String = AnalyzeResultData(
        riskLevelItem.toRiskLevelData().value
    ).toJson()

    override fun composeAppVersionNameResult(versionName: String): String {
        return AppVersionNameResult(versionName).toJson()
    }

    override fun composeSystemLanguageResult(languageISO: String): String {
        return SystemLanguageResult(languageISO).toJson()
    }

    override fun composeFontScaleResult(fontScale: Float): String {
        return FontScaleResult(fontScale).toJson()
    }

    override fun composeDistrictsRestrictionsResult(voivodeshipsItem: VoivodeshipsItem): String {
        return DistrictsRestrictionsResult(
            if (voivodeshipsItem.items.isEmpty()) {
                ResultStatus.FAILURE
            } else {
                ResultStatus.SUCCESS
            }.value,
            voivodeshipsItem.updated,
            voivodeshipsItem.items.map(VoivodeshipItem::toVoivodeshipData)
        ).toJson()
    }

    override fun composeSubscribedDistrictsResult(subscribedDistricts: List<DistrictItem>): String {
        return SubscribedDistrictsResult(subscribedDistricts.map { it.toDistrictData() }).toJson()
    }

    override fun composeUploadTestPinResult(resultStatus: ResultStatus): String {
        return SimpleResult(resultStatus.value).toJson()
    }

    override fun composeTestSubscriptionStatusResult(
        testSubscriptionItem: TestSubscriptionItem?
    ): String {
        return TestSubscriptionStatusResult(testSubscriptionItem?.toTestSubscriptionStatusData()).toJson(gson)
    }

    override fun composeTestSubscriptionPinResult(pin: String): String {
        return TestSubscriptionPinData(pin.takeIf { it.isNotEmpty() }).toJson(gson)
    }

    override fun composeBackButtonPressedResult(): String {
        return BackButtonPressedData().toJson()
    }

    override fun composeActivitiesResult(activitiesResultItem: ActivitiesResultItem): String {
        return activitiesResultItem.toActivitiesResultData().toJson()
    }

    override fun composeCovidStatsNotificationsStatusResult(areAllowed: Boolean): String {
        return CovidStatsNotificationsStatusData(areAllowed).toJson()
    }

    override fun composeENStatsResult(enStatsItem: ENStatsItem?): String {
        return ENStatsResultData(enStatsItem?.toENStatsData()).toJson(gson)
    }
}
