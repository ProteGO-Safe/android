package pl.gov.anna.information

import java.util.*

class PhoneInformation {
    val lang: String
        get() = Locale.getDefault().language

    val deviceName: String
        get() = "${android.os.Build.MANUFACTURER} ${android.os.Build.PRODUCT} ${android.os.Build.MODEL}"

    val osVersion: String
        get() = android.os.Build.VERSION.SDK_INT.toString()

    val platform: String
        get() = "android"
}