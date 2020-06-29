package pl.gov.mc.protegosafe.domain.model

interface RiskLevelConfigurationMapper {
    fun toEntity(riskLevelConfigurationJson: String): RiskLevelConfigurationItem
}
