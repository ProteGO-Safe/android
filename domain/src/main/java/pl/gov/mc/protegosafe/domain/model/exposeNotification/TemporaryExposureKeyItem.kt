package pl.gov.mc.protegosafe.domain.model.exposeNotification

data class TemporaryExposureKeyItem(
    val key: ByteArray,
    val rollingPeriod: Int,
    val rollingStartNumber: Int,
    val transmissionRisk: Int = TRANSMISSION_RISK_LEVEL_HIGHEST
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TemporaryExposureKeyItem

        if (!key.contentEquals(other.key)) return false
        if (rollingPeriod != other.rollingPeriod) return false
        if (rollingStartNumber != other.rollingStartNumber) return false
        if (transmissionRisk != other.transmissionRisk) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.contentHashCode()
        result = 31 * result + rollingPeriod
        result = 31 * result + rollingStartNumber
        result = 31 * result + transmissionRisk
        return result
    }

    companion object {
        private const val TRANSMISSION_RISK_LEVEL_HIGHEST = 8
        const val ROLLING_PERIOD_MAX = 144
    }
}
