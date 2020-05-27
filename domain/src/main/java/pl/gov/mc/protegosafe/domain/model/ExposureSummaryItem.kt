package pl.gov.mc.protegosafe.domain.model

class ExposureSummaryItem(
    val daysSinceLastExposure: Int,
    val matchedKeyCount: Int,
    val maximumRiskScore: Int,
    val attenuationDurationsInMinutes: IntArray,
    val summationRiskScore: Int
)
