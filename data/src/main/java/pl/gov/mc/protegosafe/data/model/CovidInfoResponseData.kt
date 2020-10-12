package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class CovidInfoResponseData(
    @SerializedName("updated")
    val update: Long,
    @SerializedName("voivodeships")
    val voivodeships: List<VoivodeshipData>
)
