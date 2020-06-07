package pl.gov.mc.protegosafe.data.mapper

import com.google.gson.Gson
import pl.gov.mc.protegosafe.data.model.PinData
import pl.gov.mc.protegosafe.domain.model.PinItem
import pl.gov.mc.protegosafe.domain.model.PinMapper

class PinMapperImpl : PinMapper {
    override fun toEntity(json: String): PinItem =
        Gson().fromJson(json, PinData::class.java).toEntity()
}
