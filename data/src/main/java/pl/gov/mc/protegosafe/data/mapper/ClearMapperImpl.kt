package pl.gov.mc.protegosafe.data.mapper

import com.google.gson.Gson
import pl.gov.mc.protegosafe.data.model.ClearData
import pl.gov.mc.protegosafe.domain.model.ClearItem
import pl.gov.mc.protegosafe.domain.model.ClearMapper

class ClearMapperImpl : ClearMapper {
    override fun toEntity(json: String): ClearItem =
        Gson().fromJson(json, ClearData::class.java).toEntity()
}
