package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class RouteData(
    @SerializedName("name")
    val name: String,
    @SerializedName("params")
    var params: MutableMap<String, String> = mutableMapOf()
)
