package pl.gov.mc.protegosafe.domain

import pl.gov.mc.protegosafe.domain.model.VoivodeshipItem

data class CovidInfoItem(
    val lastUpdate: Long,
    val voivodeships: List<VoivodeshipItem>
)
