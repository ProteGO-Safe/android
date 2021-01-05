package pl.gov.mc.protegosafe.domain.model

data class DeleteActivitiesItem(
    val notifications: List<String>,
    val riskChecks: List<String>,
    val exposures: List<String>,
)
