package pl.gov.mc.protegosafe.domain.model

data class DistrictItem(
    val id: Int,
    val name: String,
    val state: DistrictRestrictionStateItem
)
