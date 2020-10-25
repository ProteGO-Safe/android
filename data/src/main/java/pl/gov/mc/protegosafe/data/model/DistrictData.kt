package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class DistrictData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("state")
    val state: Int
)
