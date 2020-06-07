package pl.gov.mc.protegosafe.data.mapper

import com.google.gson.Gson
import pl.gov.mc.protegosafe.data.model.DiagnosisKeyDownloadConfigurationData
import pl.gov.mc.protegosafe.domain.model.DiagnosisKeyDownloadConfiguration
import pl.gov.mc.protegosafe.domain.model.DiagnosisKeyDownloadConfigurationMapper

class DiagnosisKeyDownloadConfigurationMapperImpl :
    DiagnosisKeyDownloadConfigurationMapper {
    override fun toEntity(configurationJson: String): DiagnosisKeyDownloadConfiguration {
        return Gson().fromJson(configurationJson, DiagnosisKeyDownloadConfigurationData::class.java)
            .toEntity()
    }
}
