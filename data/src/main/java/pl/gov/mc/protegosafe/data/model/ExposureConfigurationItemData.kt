package pl.gov.mc.protegosafe.data.model

import com.google.gson.annotations.SerializedName

data class ExposureConfigurationItemData(
    @SerializedName("minimumRiskScore")
    val minimumRiskScore: Int,
    @SerializedName("attenuationScores")
    val attenuationScores: IntArray,
    @SerializedName("attenuationWeigh")
    val attenuationWeigh: Int,
    @SerializedName("daysSinceLastExposureScores")
    val daysSinceLastExposureScores: IntArray,
    @SerializedName("daysSinceLastExposureWeight")
    val daysSinceLastExposureWeight: Int,
    @SerializedName("durationScores")
    val durationScores: IntArray,
    @SerializedName("durationWeight")
    val durationWeight: Int,
    @SerializedName("transmissionRiskScores")
    val transmissionRiskScores: IntArray,
    @SerializedName("transmissionRiskWeight")
    val transmissionRiskWeight: Int,
    @SerializedName("durationAtAttenuationThresholds")
    val durationAtAttenuationThresholds: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ExposureConfigurationItemData

        if (minimumRiskScore != other.minimumRiskScore) return false
        if (!attenuationScores.contentEquals(other.attenuationScores)) return false
        if (attenuationWeigh != other.attenuationWeigh) return false
        if (!daysSinceLastExposureScores.contentEquals(other.daysSinceLastExposureScores)) return false
        if (daysSinceLastExposureWeight != other.daysSinceLastExposureWeight) return false
        if (!durationScores.contentEquals(other.durationScores)) return false
        if (durationWeight != other.durationWeight) return false
        if (!transmissionRiskScores.contentEquals(other.transmissionRiskScores)) return false
        if (transmissionRiskWeight != other.transmissionRiskWeight) return false
        if (!durationAtAttenuationThresholds.contentEquals(other.durationAtAttenuationThresholds)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = minimumRiskScore
        result = 31 * result + attenuationScores.contentHashCode()
        result = 31 * result + attenuationWeigh
        result = 31 * result + daysSinceLastExposureScores.contentHashCode()
        result = 31 * result + daysSinceLastExposureWeight
        result = 31 * result + durationScores.contentHashCode()
        result = 31 * result + durationWeight
        result = 31 * result + transmissionRiskScores.contentHashCode()
        result = 31 * result + transmissionRiskWeight
        result = 31 * result + durationAtAttenuationThresholds.contentHashCode()
        return result
    }
}
