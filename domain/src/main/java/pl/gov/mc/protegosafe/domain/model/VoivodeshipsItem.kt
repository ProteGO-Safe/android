package pl.gov.mc.protegosafe.domain.model

data class VoivodeshipsItem(
    val updated: Long = 0L,
    val items: List<VoivodeshipItem> = emptyList()
)
