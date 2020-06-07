package pl.gov.mc.protegosafe.domain.model

class ExposureConfigurationItem(
    val minimumRiskScore: Int,
    val attenuationScores: IntArray,
    val attenuationWeigh: Int,
    val daysSinceLastExposureScores: IntArray,
    val daysSinceLastExposureWeight: Int,
    val durationScores: IntArray,
    val durationWeight: Int,
    val transmissionRiskScores: IntArray,
    val transmissionRiskWeight: Int,
    val durationAtAttenuationThresholds: IntArray
)
