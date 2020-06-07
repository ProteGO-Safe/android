package pl.gov.mc.protegosafe.domain.model

interface ClearMapper {
    fun toEntity(json: String): ClearItem
}
