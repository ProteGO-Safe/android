package pl.gov.mc.protegosafe.domain.model

data class VoivodeshipItem(
    val id: Int,
    val name: String,
    val districts: List<DistrictItem>
)
