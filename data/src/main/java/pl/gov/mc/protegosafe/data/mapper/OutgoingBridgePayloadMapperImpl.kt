package pl.gov.mc.protegosafe.data.mapper

import com.google.gson.Gson
import pl.gov.mc.protegosafe.data.model.DistrictActionData
import pl.gov.mc.protegosafe.domain.model.DistrictActionItem
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgePayloadMapper

class OutgoingBridgePayloadMapperImpl : OutgoingBridgePayloadMapper {
    override fun toDistrictActionItem(payload: String): DistrictActionItem {
        return Gson().fromJson(payload, DistrictActionData::class.java).toEntity()
    }
}
