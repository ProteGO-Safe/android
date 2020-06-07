package pl.gov.mc.protegosafe.domain.model

interface ChangeServiceStatusRequestMapper {
    fun toDomain(json: String): List<ChangeStatusRequestItem>
}
