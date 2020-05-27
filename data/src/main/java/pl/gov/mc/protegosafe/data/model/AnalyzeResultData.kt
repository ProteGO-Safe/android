package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

class AnalyzeResultData(
    @SerializedName("riskLevel")
    val riskLevel: Int
)
