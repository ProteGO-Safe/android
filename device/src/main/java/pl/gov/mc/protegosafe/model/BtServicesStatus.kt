package pl.gov.mc.protegosafe.model

import com.google.gson.annotations.SerializedName

data class BtServicesStatusRoot(
    @SerializedName("btServiceStatus")
    val btServiceStatus: BtServicesStatus
)

data class BtServicesStatus(
    @SerializedName("tempId")
    val tempId: String
)