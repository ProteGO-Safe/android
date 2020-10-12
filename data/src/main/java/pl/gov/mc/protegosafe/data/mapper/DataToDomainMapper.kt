package pl.gov.mc.protegosafe.data.mapper

import com.google.android.gms.nearby.exposurenotification.ExposureInformation
import com.google.android.gms.nearby.exposurenotification.ExposureSummary
import com.google.android.gms.nearby.exposurenotification.TemporaryExposureKey
import pl.gov.mc.protegosafe.data.BuildConfig
import pl.gov.mc.protegosafe.data.model.ClearData
import pl.gov.mc.protegosafe.data.model.CloseAppData
import pl.gov.mc.protegosafe.data.model.DiagnosisKeyDownloadConfigurationData
import pl.gov.mc.protegosafe.data.model.DistrictData
import pl.gov.mc.protegosafe.data.model.DistrictDto
import pl.gov.mc.protegosafe.data.model.ExposureConfigurationItemData
import pl.gov.mc.protegosafe.data.model.ExposureDto
import pl.gov.mc.protegosafe.data.model.CovidInfoResponseData
import pl.gov.mc.protegosafe.data.model.DistrictActionData
import pl.gov.mc.protegosafe.data.model.PinData
import pl.gov.mc.protegosafe.data.model.RiskLevelConfigurationData
import pl.gov.mc.protegosafe.data.model.TriageData
import pl.gov.mc.protegosafe.data.model.VoivodeshipData
import pl.gov.mc.protegosafe.data.model.VoivodeshipDto
import pl.gov.mc.protegosafe.domain.CovidInfoItem
import pl.gov.mc.protegosafe.domain.model.ClearItem
import pl.gov.mc.protegosafe.domain.model.CloseAppItem
import pl.gov.mc.protegosafe.domain.model.DiagnosisKeyDownloadConfiguration
import pl.gov.mc.protegosafe.domain.model.DistrictActionItem
import pl.gov.mc.protegosafe.domain.model.DistrictItem
import pl.gov.mc.protegosafe.domain.model.DistrictRestrictionStateItem
import pl.gov.mc.protegosafe.domain.model.ExposureConfigurationItem
import pl.gov.mc.protegosafe.domain.model.ExposureInformationItem
import pl.gov.mc.protegosafe.domain.model.ExposureItem
import pl.gov.mc.protegosafe.domain.model.ExposureSummaryItem
import pl.gov.mc.protegosafe.domain.model.PinItem
import pl.gov.mc.protegosafe.domain.model.PushNotificationData
import pl.gov.mc.protegosafe.domain.model.PushNotificationTopic
import pl.gov.mc.protegosafe.domain.model.RiskLevelConfigurationItem
import pl.gov.mc.protegosafe.domain.model.TriageItem
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeyItem
import pl.gov.mc.protegosafe.domain.model.VoivodeshipItem

private const val FCM_NOTIFICATION_TITLE_KEY = "title"
private const val FCM_NOTIFICATION_CONTENT_KEY = "content"
fun Map<String, String>.hasNotification() =
    !get(FCM_NOTIFICATION_TITLE_KEY).isNullOrBlank()

fun Map<String, String>.toNotificationDataItem(topic: String?) = PushNotificationData(
    title = get(FCM_NOTIFICATION_TITLE_KEY)
        ?: throw IllegalArgumentException("Hash id has no value"),
    content = get(FCM_NOTIFICATION_CONTENT_KEY) ?: "",
    topic = when (topic) {
        "/topics/${BuildConfig.MAIN_TOPIC}" -> PushNotificationTopic.MAIN
        "/topics/${BuildConfig.DAILY_TOPIC}" -> PushNotificationTopic.DAILY
        else -> PushNotificationTopic.UNKNOWN
    }
)

fun TriageData.toEntity() = TriageItem(timestamp = timestamp)

fun ClearData.toEntity() = ClearItem(clearBtData = clearBtData)

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
