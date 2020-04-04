package pl.gov.mc.protego.bluetooth.beacon


data class BeaconId(val byteArray: ByteArray, val version: Int) {

    companion object {
        const val byteCount = 16
    }

    init {
        check(byteArray.size == byteCount) { "Invalid BeaconId length" }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BeaconId) return false

        if (!byteArray.contentEquals(other.byteArray)) return false

        return true
    }

    override fun hashCode(): Int {
        return byteArray.contentHashCode()
    }
}