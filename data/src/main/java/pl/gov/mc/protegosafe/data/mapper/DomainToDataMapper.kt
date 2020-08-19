package pl.gov.mc.protegosafe.data.mapper

import com.google.android.gms.nearby.exposurenotification.ExposureConfiguration
import pl.gov.mc.protegosafe.data.extension.toBase64
import pl.gov.mc.protegosafe.data.model.ExposureDto
import pl.gov.mc.protegosafe.data.model.TemporaryExposureKeyRequestData
import pl.gov.mc.protegosafe.data.model.TemporaryExposureKeysUploadRequestBody
import pl.gov.mc.protegosafe.domain.model.ExposureConfigurationItem
import pl.gov.mc.protegosafe.domain.model.ExposureItem
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeyItem
import pl.gov.mc.protegosafe.domain.model.TemporaryExposureKeysUploadRequestItem

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

fun TemporaryExposureKeysUploadRequestItem.toTemporaryExposureKeysUploadRequestBody(): TemporaryExposureKeysUploadRequestBody =
    TemporaryExposureKeysUploadRequestBody(
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
