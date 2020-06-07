package pl.gov.mc.protegosafe.data.mapper

import com.google.gson.Gson
import pl.gov.mc.protegosafe.data.model.ExposureConfigurationItemData
import pl.gov.mc.protegosafe.domain.model.ExposureConfigurationItem
import pl.gov.mc.protegosafe.domain.model.ExposureConfigurationMapper

class ExposureConfigurationMapperImpl : ExposureConfigurationMapper {
    override fun toEntity(exposureConfigurationJson: String): ExposureConfigurationItem {
        return Gson().fromJson(exposureConfigurationJson, ExposureConfigurationItemData::class.java)
            .toEntity()
    }
}
