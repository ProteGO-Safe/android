package pl.gov.mc.protegosafe.data.mapper

import com.google.android.gms.nearby.exposurenotification.ExposureInformation
import com.google.android.gms.nearby.exposurenotification.ExposureSummary
import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey
import pl.gov.mc.protegosafe.data.model.AppReviewData
import pl.gov.mc.protegosafe.data.model.ClearData
import pl.gov.mc.protegosafe.data.model.CloseAppData
import pl.gov.mc.protegosafe.data.model.DiagnosisKeyDownloadConfigurationData
import pl.gov.mc.protegosafe.data.model.DistrictData
import pl.gov.mc.protegosafe.data.model.DistrictDto
import pl.gov.mc.protegosafe.data.model.ExposureConfigurationItemData
import pl.gov.mc.protegosafe.data.model.ExposureDto
import pl.gov.mc.protegosafe.data.model.CovidInfoResponseData
import pl.gov.mc.protegosafe.data.model.DistrictActionData
import pl.gov.mc.protegosafe.data.model.ExposureCheckActivityDto
import pl.gov.mc.protegosafe.data.model.InteroperabilityData
import pl.gov.mc.protegosafe.data.model.NotificationActivityDto
import pl.gov.mc.protegosafe.data.model.covidtest.TestSubscriptionDto
import pl.gov.mc.protegosafe.data.model.PinData
import pl.gov.mc.protegosafe.data.model.RiskCheckActivityDto
import pl.gov.mc.protegosafe.data.model.RiskLevelConfigurationData
import pl.gov.mc.protegosafe.data.model.RiskLevelData
import pl.gov.mc.protegosafe.data.model.TriageData
import pl.gov.mc.protegosafe.data.model.VoivodeshipData
import pl.gov.mc.protegosafe.data.model.VoivodeshipDto
import pl.gov.mc.protegosafe.data.model.covidtest.TestSubscriptionConfigurationData
import pl.gov.mc.protegosafe.domain.CovidInfoItem
import pl.gov.mc.protegosafe.domain.model.AppReviewItem
import pl.gov.mc.protegosafe.domain.model.ClearItem
import pl.gov.mc.protegosafe.domain.model.CloseAppItem
import pl.gov.mc.protegosafe.domain.model.DiagnosisKeyDownloadConfiguration
import pl.gov.mc.protegosafe.domain.model.DistrictActionItem
import pl.gov.mc.protegosafe.domain.model.DistrictItem
import pl.gov.mc.protegosafe.domain.model.DistrictRestrictionStateItem
import pl.gov.mc.protegosafe.domain.model.ExposureCheckActivityItem
import pl.gov.mc.protegosafe.domain.model.ExposureConfigurationItem
import pl.gov.mc.protegosafe.domain.model.ExposureInformationItem
import pl.gov.mc.protegosafe.domain.model.ExposureItem
import pl.gov.mc.protegosafe.domain.model.ExposureSummaryItem
import pl.gov.mc.protegosafe.domain.model.InteroperabilityItem
import pl.gov.mc.protegosafe.domain.model.TestSubscriptionItem
import pl.gov.mc.protegosafe.domain.model.TestSubscriptionStatus
import pl.gov.mc.protegosafe.domain.model.PinItem
import pl.gov.mc.protegosafe.domain.model.PushNotificationItem
import pl.gov.mc.protegosafe.domain.model.RiskCheckActivityItem
import pl.gov.mc.protegosafe.domain.model.RiskLevelConfigurationItem
import pl.gov.mc.protegosafe.domain.model.RiskLevelItem
import pl.gov.mc.protegosafe.domain.model.TriageItem
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeyItem
import pl.gov.mc.protegosafe.domain.model.TestSubscriptionConfigurationItem
import pl.gov.mc.protegosafe.domain.model.VoivodeshipItem

fun TriageData.toEntity() = TriageItem(timestamp = timestamp)

fun ClearData.toEntity() = ClearItem(clearAll = clearAll)

fun CloseAppData.toEntity() = CloseAppItem(turnOff = turnOff)

fun ExposureConfigurationItemData.toEntity() = ExposureConfigurationItem(
    minimumRiskScore = minimumRiskScore,
    attenuationScores = attenuationScores,
    attenuationWeigh = attenuationWeigh,
    daysSinceLastExposureScores = daysSinceLastExposureScores,
    daysSinceLastExposureWeight = daysSinceLastExposureWeight,
    durationScores = durationScores,
    durationWeight = durationWeight,
    transmissionRiskScores = transmissionRiskScores,
    transmissionRiskWeight = transmissionRiskWeight,
    durationAtAttenuationThresholds = durationAtAttenuationThresholds
)

fun DiagnosisKeyDownloadConfigurationData.toEntity() = DiagnosisKeyDownloadConfiguration(
    timeoutMobileSeconds = timeoutMobileSeconds,
    timeoutWifiSeconds = timeoutWifiSeconds,
    retryCount = retryCount
)

fun PinData.toEntity() = PinItem(pin = pin)

fun InteroperabilityData.toEntity() = InteroperabilityItem(
    isInteroperabilityEnabled = isInteroperabilityEnabled
)

fun DistrictData.toEntity() = DistrictItem(
    id = id,
    name = name,
    state = DistrictRestrictionStateItem.valueOf(state)
)

fun VoivodeshipData.toEntity() = VoivodeshipItem(
    id = id,
    name = name,
    districts = districts.map { it.toEntity() }
)

fun CovidInfoResponseData.toEntity() = CovidInfoItem(
    lastUpdate = update,
    voivodeships = voivodeships.map { it.toEntity() }
)

/**
 * Map Temporary Exposure Key to entity model.
 * Function contains fix for known Exposure Notification API issue!
 * If [TemporaryExposureKey.getRollingPeriod] == 0, [TemporaryExposureKeyItem.rollingPeriod] = 144
 */
fun TemporaryExposureKey.toEntity() =
    TemporaryExposureKeyItem(
        key = keyData,
        rollingPeriod = rollingPeriod,
        rollingStartNumber = rollingStartIntervalNumber
    )

fun ExposureSummary.toEntity() = ExposureSummaryItem(
    daysSinceLastExposure = daysSinceLastExposure,
    matchedKeyCount = matchedKeyCount,
    attenuationDurationsInMinutes = attenuationDurationsInMinutes,
    maximumRiskScore = maximumRiskScore,
    summationRiskScore = summationRiskScore
)

fun ExposureInformation.toEntity() = ExposureInformationItem(
    dateMillisSinceEpoch = dateMillisSinceEpoch,
    attenuationDurations = attenuationDurationsInMinutes,
    attenuationValue = attenuationValue,
    durationMinutes = durationMinutes,
    totalRiskScore = totalRiskScore,
    transmissionRiskLevel = transmissionRiskLevel
)

fun ExposureDto.toEntity() = ExposureItem(
    date = date,
    riskScore = riskScore,
    durationInMinutes = durationInMinutes
)

fun RiskLevelConfigurationData.toEntity() = RiskLevelConfigurationItem(
    maxNoRiskScore = maxNoRiskScore,
    maxLowRiskScore = maxLowRiskScore,
    maxMiddleRiskScore = maxMiddleRiskScore
)

fun DistrictDto.toEntity() = DistrictItem(
    id = id,
    name = name,
    state = DistrictRestrictionStateItem.valueOf(state)
)

fun VoivodeshipDto.toEntity() = VoivodeshipItem(
    id = id,
    name = name,
    districts = districts.map { it.toEntity() }
)

fun DistrictActionData.toEntity() = DistrictActionItem(
    type = DistrictActionItem.ActionType.valueOf(type),
    districtId = districtId
)

fun TestSubscriptionDto.toEntity() = TestSubscriptionItem(
    guid = guid,
    status = TestSubscriptionStatus.valueOf(status),
    updated = updated,
    accessToken = accessToken
)

fun RiskLevelData.toEntity() = when (this) {
    RiskLevelData.NO_RISK -> RiskLevelItem.NO_RISK
    RiskLevelData.LOW_RISK -> RiskLevelItem.LOW_RISK
    RiskLevelData.MIDDLE_RISK -> RiskLevelItem.MIDDLE_RISK
    RiskLevelData.HIGH_RISK -> RiskLevelItem.HIGH_RISK
}

fun TestSubscriptionConfigurationData.toEntity() = TestSubscriptionConfigurationItem(
    interval = interval
)

fun AppReviewData.toEntity() = AppReviewItem(
    appReview = appReview
)

fun RiskCheckActivityDto.toEntity() = RiskCheckActivityItem(
    id = id,
    keys = keys,
    exposures = exposures,
    timestamp = timestamp
)

fun ExposureCheckActivityDto.toEntity() = ExposureCheckActivityItem(
    id = id,
    riskLevel = RiskLevelItem.valueOf(riskLevel),
    exposures = exposures,
    timestamp = timestamp
)

fun NotificationActivityDto.toEntity() = PushNotificationItem(
    id = id,
    title = title,
    content = content,
    timestamp = timestamp
)
