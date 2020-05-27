package pl.gov.mc.protegosafe.domain.model.exposeNotification

data class DiagnosisKey(
    val key: ByteArray,
    val intervalNumber: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DiagnosisKey

        if (!key.contentEquals(other.key)) return false
        if (intervalNumber != other.intervalNumber) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key.contentHashCode()
        result = 31 * result + intervalNumber
        return result
    }
}
