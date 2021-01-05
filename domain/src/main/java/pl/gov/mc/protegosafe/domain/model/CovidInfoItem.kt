package pl.gov.mc.protegosafe.domain.model

data class CovidInfoItem(
    val voivodeshipsUpdated: Long,
    val voivodeships: List<VoivodeshipItem>,
    val covidStatsItem: CovidStatsItem
)
