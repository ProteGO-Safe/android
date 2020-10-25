package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

class DistrictActionData(
    @SerializedName("type")
    val type: Int,
    @SerializedName("districtId")
    val districtId: Int
)
