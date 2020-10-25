package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class VoivodeshipData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("districts")
    val districts: List<DistrictData>
)
