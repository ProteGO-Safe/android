package pl.gov.mc.protegosafe.domain.model

data class ExposureCheckActivityItem(
    val riskLevel: RiskLevelItem,
    val exposures: Int
)
