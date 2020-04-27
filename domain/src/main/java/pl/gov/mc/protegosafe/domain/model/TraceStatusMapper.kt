package pl.gov.mc.protegosafe.domain.model

interface TraceStatusMapper {
    fun toDomainItem(json: String): TraceStatusItem
}