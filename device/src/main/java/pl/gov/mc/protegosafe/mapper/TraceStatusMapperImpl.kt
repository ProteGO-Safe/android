package pl.gov.mc.protegosafe.mapper

import com.google.gson.Gson
import pl.gov.mc.protegosafe.domain.model.TraceStatusItem
import pl.gov.mc.protegosafe.domain.model.TraceStatusMapper
import pl.gov.mc.protegosafe.model.TraceStatusDto

class TraceStatusMapperImpl : TraceStatusMapper {

    override fun toDomainItem(json: String): TraceStatusItem =
        Gson().fromJson(json, TraceStatusDto::class.java).toDomainItem()
}