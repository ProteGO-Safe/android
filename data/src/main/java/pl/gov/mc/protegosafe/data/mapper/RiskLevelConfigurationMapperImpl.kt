package pl.gov.mc.protegosafe.data.mapper

import com.google.gson.Gson
import pl.gov.mc.protegosafe.data.model.RiskLevelConfigurationData
import pl.gov.mc.protegosafe.domain.model.RiskLevelConfigurationItem
import pl.gov.mc.protegosafe.domain.model.RiskLevelConfigurationMapper

class RiskLevelConfigurationMapperImpl : RiskLevelConfigurationMapper {
    override fun toEntity(riskLevelConfigurationJson: String): RiskLevelConfigurationItem {
        return Gson().fromJson(riskLevelConfigurationJson, RiskLevelConfigurationData::class.java)
            .toEntity()
    }
}
