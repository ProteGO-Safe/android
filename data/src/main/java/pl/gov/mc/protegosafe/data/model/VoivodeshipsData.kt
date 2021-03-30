package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class VoivodeshipsData(
    @SerializedName("updated") val updated: Long,
    @SerializedName("voivodeships") val voivodeships: List<VoivodeshipData>
)
