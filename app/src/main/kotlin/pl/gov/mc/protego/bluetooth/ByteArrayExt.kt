package pl.gov.mc.protego.bluetooth

fun ByteArray.toHexString() : String {
    return this.joinToString("") {
        String.format("%02x", it)
    }
}