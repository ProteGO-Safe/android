package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

class DistrictsRestrictionsResult(
    @SerializedName("result")
    val result: Int,
    @SerializedName("updated")
    val updated: Long,
    @SerializedName("voivodeships")
    val voivodeships: List<VoivodeshipData>
)
