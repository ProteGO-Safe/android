package pl.gov.mc.protegosafe.domain.model

data class ActivitiesResultItem(
    val riskChecks: List<RiskCheckActivityItem>,
    val exposures: List<ExposureCheckActivityItem>
)
