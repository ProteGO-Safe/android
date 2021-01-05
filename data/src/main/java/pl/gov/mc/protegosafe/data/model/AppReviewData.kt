package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class AppReviewData(
    @SerializedName("appReview")
    val appReview: Boolean
)
