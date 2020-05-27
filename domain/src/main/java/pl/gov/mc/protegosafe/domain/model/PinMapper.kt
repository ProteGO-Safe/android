package pl.gov.mc.protegosafe.domain.model

interface PinMapper {
    fun toEntity(json: String): PinItem
}
