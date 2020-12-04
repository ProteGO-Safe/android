package pl.gov.mc.protegosafe.data.mapper

import com.google.android.gms.nearby.exposurenotification.ExposureConfiguration
import io.realm.RealmList
import pl.gov.mc.protegosafe.data.extension.toBase64
import pl.gov.mc.protegosafe.data.model.DistrictData
import pl.gov.mc.protegosafe.data.model.DistrictDto
import pl.gov.mc.protegosafe.data.model.ExposureCheckActivityDto
import pl.gov.mc.protegosafe.data.model.ExposureDto
import pl.gov.mc.protegosafe.data.model.RiskLevelData
import pl.gov.mc.protegosafe.data.model.TemporaryExposureKeyRequestData
import pl.gov.mc.protegosafe.data.model.TemporaryExposureKeysUploadData
import pl.gov.mc.protegosafe.data.model.UINotificationData
import pl.gov.mc.protegosafe.data.model.VoivodeshipData
import pl.gov.mc.protegosafe.data.model.VoivodeshipDto
import pl.gov.mc.protegosafe.data.model.covidtest.TestSubscriptionDto
import pl.gov.mc.protegosafe.data.model.covidtest.TestSubscriptionStatusData
import pl.gov.mc.protegosafe.domain.model.DistrictItem
import pl.gov.mc.protegosafe.domain.model.ExposureCheckActivityItem
import pl.gov.mc.protegosafe.domain.model.ExposureConfigurationItem
import pl.gov.mc.protegosafe.domain.model.ExposureItem
import pl.gov.mc.protegosafe.domain.model.PushNotificationItem
import pl.gov.mc.protegosafe.domain.model.RiskLevelItem
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeyItem
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeysUploadRequestItem
import pl.gov.mc.protegosafe.domain.model.TestSubscriptionItem
import pl.gov.mc.protegosafe.domain.model.VoivodeshipItem

fun ExposureConfigurationItem.toExposureConfiguration(): ExposureConfiguration {
    return ExposureConfiguration.ExposureConfigurationBuilder()
        .setMinimumRiskScore(this.minimumRiskScore)
        .setAttenuationScores(*this.attenuationScores)
        .setAttenuationWeight(this.attenuationWeigh)
        .setDaysSinceLastExposureScores(*this.daysSinceLastExposureScores)
        .setDaysSinceLastExposureWeight(this.daysSinceLastExposureWeight)
        .setDurationScores(*this.durationScores)
        .setDurationWeight(this.durationWeight)
        .setTransmissionRiskScores(*this.transmissionRiskScores)
        .setTransmissionRiskWeight(this.transmissionRiskWeight)
        .setDurationAtAttenuationThresholds(*this.durationAtAttenuationThresholds)
        .build()
}

fun TemporaryExposureKeyItem.toTemporaryExposureKeyRequestData(): TemporaryExposureKeyRequestData =
    TemporaryExposureKeyRequestData(
        base64Key = key.toBase64(),
        rollingStartNumber = rollingStartNumber,
        rollingPeriod = rollingPeriod,
        transmissionRisk = transmissionRisk
    )

fun List<TemporaryExposureKeyItem>.toTemporaryExposureKeyRequestDataList(): List<TemporaryExposureKeyRequestData> =
    map { it.toTemporaryExposureKeyRequestData() }

fun TemporaryExposureKeysUploadRequestItem.toTemporaryExposureKeysUploadData(): TemporaryExposureKeysUploadData =
    TemporaryExposureKeysUploadData(
        temporaryExposureKeys = keys.toTemporaryExposureKeyRequestDataList(),
        platform = platform,
        appPackageName = appPackageName,
        regions = regions,
        verificationPayload = verificationPayload
    )

fun ExposureItem.toExposureDto() = ExposureDto(
    date = date,
    durationInMinutes = durationInMinutes,
    riskScore = riskScore
)

fun DistrictItem.toDistrictDto() = DistrictDto(
    id = id,
    name = name,
    state = state.value
)

fun VoivodeshipItem.toVoivodeshipDto() = VoivodeshipDto(
    id = id,
    name = name,
    districts = districts
        .map { it.toDistrictDto() }
        .toCollection(RealmList<DistrictDto>())
)

fun DistrictItem.toDistrictData() = DistrictData(
    id = id,
    name = name,
    state = state.value
)

fun VoivodeshipItem.toVoivodeshipData() = VoivodeshipData(
    id = id,
    name = name,
    districts = districts.map { it.toDistrictData() }
)

fun TestSubscriptionItem.toTestSubscriptionStatusData() = TestSubscriptionStatusData(
    guid = guid,
    status = status.value,
    updated = updated
)

fun TestSubscriptionItem.toTestSubscriptionDto() = TestSubscriptionDto(
    guid = guid,
    status = status.value,
    updated = updated,
    accessToken = accessToken
)

fun RiskLevelItem.toRiskLevelData() = when (this) {
    RiskLevelItem.NO_RISK -> RiskLevelData.NO_RISK
    RiskLevelItem.LOW_RISK -> RiskLevelData.LOW_RISK
    RiskLevelItem.MIDDLE_RISK -> RiskLevelData.MIDDLE_RISK
    RiskLevelItem.HIGH_RISK -> RiskLevelData.HIGH_RISK
}

fun PushNotificationItem.toUINotificationData() = UINotificationData(
    title = title,
    content = content,
    status = status
)

fun ExposureCheckActivityItem.toExposureCheckActivityDto() = ExposureCheckActivityDto(
    riskLevel = riskLevel.toRiskLevelData().value,
    exposures = exposures
)
