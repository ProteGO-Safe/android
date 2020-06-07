package pl.gov.mc.protegosafe.domain.model

interface ExposureConfigurationMapper {
    fun toEntity(exposureConfigurationJson: String): ExposureConfigurationItem
}
