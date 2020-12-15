package pl.gov.mc.protegosafe.data.model

import com.google.gson.GsonBuilder
import pl.gov.mc.protegosafe.data.extension.toJson
import pl.gov.mc.protegosafe.data.mapper.toActivitiesResultData
import pl.gov.mc.protegosafe.data.mapper.toCovidStatsData
import pl.gov.mc.protegosafe.data.mapper.toDistrictData
import pl.gov.mc.protegosafe.data.mapper.toTestSubscriptionStatusData
import pl.gov.mc.protegosafe.data.mapper.toRiskLevelData
import pl.gov.mc.protegosafe.data.mapper.toVoivodeshipData
import pl.gov.mc.protegosafe.data.model.covidtest.TestSubscriptionStatusResult
import pl.gov.mc.protegosafe.domain.model.ActivitiesResultItem
import pl.gov.mc.protegosafe.domain.model.AppLifecycleState
import pl.gov.mc.protegosafe.domain.model.CovidStatsItem
import pl.gov.mc.protegosafe.domain.model.DistrictItem
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgeDataResultComposer
import pl.gov.mc.protegosafe.domain.model.ResultStatus
import pl.gov.mc.protegosafe.domain.model.RiskLevelItem
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeysUploadState
import pl.gov.mc.protegosafe.domain.model.TestSubscriptionItem
import pl.gov.mc.protegosafe.domain.model.VoivodeshipItem

class OutgoingBridgeDataResultComposerImpl : OutgoingBridgeDataResultComposer {

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

    override fun composeDistrictsRestrictionsResult(
        voivodeships: List<VoivodeshipItem>,
        updated: Long
    ): String {
        return DistrictsRestrictionsResult(
            if (voivodeships.isEmpty()) {
                ResultStatus.FAILURE
            } else {
                ResultStatus.SUCCESS
            }.value,
            updated,
            voivodeships.map { it.toVoivodeshipData() }
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
        return GsonBuilder().serializeNulls().create()
            .toJson(
                TestSubscriptionStatusResult(
                    testSubscriptionItem?.toTestSubscriptionStatusData()
                )
            )
    }

    override fun composeTestSubscriptionPinResult(pin: String): String {
        return GsonBuilder().serializeNulls().create()
            .toJson(
                TestSubscriptionPinData(
                    if (pin.isEmpty()) {
                        null
                    } else {
                        pin
                    }
                )
            )
    }

    override fun composeBackButtonPressedResult(): String {
        return BackButtonPressedData().toJson()
    }

    override fun composeActivitiesResult(activitiesResultItem: ActivitiesResultItem): String {
        return activitiesResultItem.toActivitiesResultData().toJson()
    }

    override fun composeCovidStatsResult(covidStatsItem: CovidStatsItem): String {
        return GsonBuilder().serializeNulls().create()
            .toJson(
                CovidStatsResultData(
                    if (covidStatsItem.updated == 0L) {
                        null
                    } else {
                        covidStatsItem.toCovidStatsData()
                    }
                )
            )
    }

    override fun composeCovidStatsNotificationsStatusResult(areAllowed: Boolean): String {
        return CovidStatsNotificationsStatusData(areAllowed).toJson()
    }
}
