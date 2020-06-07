package pl.gov.mc.protegosafe.domain.extension

fun ByteArray.toHexString(): String {
    val sb = StringBuilder(size * 2)
    for (j in indices) {
        sb.append(String.format("%02X", this[j]))
    }
    return sb.toString()
}
