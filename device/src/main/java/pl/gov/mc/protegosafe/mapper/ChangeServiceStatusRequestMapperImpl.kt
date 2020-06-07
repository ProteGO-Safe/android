package pl.gov.mc.protegosafe.mapper

import com.google.gson.Gson
import pl.gov.mc.protegosafe.domain.model.ChangeServiceStatusRequestMapper
import pl.gov.mc.protegosafe.domain.model.ChangeStatusRequestItem
import pl.gov.mc.protegosafe.model.ChangeServiceStatusRequestData

class ChangeServiceStatusRequestMapperImpl : ChangeServiceStatusRequestMapper {

    override fun toDomain(json: String): List<ChangeStatusRequestItem> =
        Gson().fromJson(json, ChangeServiceStatusRequestData::class.java).toDomainItem()
}
