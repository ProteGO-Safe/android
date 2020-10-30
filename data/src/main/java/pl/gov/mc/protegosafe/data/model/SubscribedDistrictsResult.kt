package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class SubscribedDistrictsResult(
    @SerializedName("districts")
    val districts: List<DistrictData>
)
