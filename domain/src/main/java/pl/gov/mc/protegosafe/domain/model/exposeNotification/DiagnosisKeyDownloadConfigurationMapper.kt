package pl.gov.mc.protegosafe.domain.model.exposeNotification

import pl.gov.mc.protegosafe.domain.model.DiagnosisKeyDownloadConfiguration

interface DiagnosisKeyDownloadConfigurationMapper {
    fun toEntity(configurationJson: String): DiagnosisKeyDownloadConfiguration
}
