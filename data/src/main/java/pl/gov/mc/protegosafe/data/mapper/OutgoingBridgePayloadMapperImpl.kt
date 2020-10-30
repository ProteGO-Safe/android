package pl.gov.mc.protegosafe.data.mapper

import com.google.gson.Gson
import pl.gov.mc.protegosafe.data.model.DistrictActionData
import pl.gov.mc.protegosafe.data.model.PinData
import pl.gov.mc.protegosafe.domain.model.DistrictActionItem
import pl.gov.mc.protegosafe.domain.model.OutgoingBridgePayloadMapper
import pl.gov.mc.protegosafe.domain.model.PinItem

class OutgoingBridgePayloadMapperImpl() : OutgoingBridgePayloadMapper {
    override fun toDistrictActionItem(payload: String): DistrictActionItem {
        return Gson().fromJson(payload, DistrictActionData::class.java).toEntity()
    }

    override fun toPinItem(payload: String): PinItem {
        return Gson().fromJson(payload, PinData::class.java).toEntity()
    }
}
