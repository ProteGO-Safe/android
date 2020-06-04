package pl.gov.mc.protegosafe.domain.model

interface DiagnosisKeyDownloadConfigurationMapper {
    fun toEntity(configurationJson: String): DiagnosisKeyDownloadConfiguration
}
