package pl.gov.mc.protego.information

import pl.gov.mc.protego.BuildConfig

class AppInformation {
    val api: Int
        get() = BuildConfig.SUPPORTED_API_VERSION

    val version: Int
        get() = BuildConfig.VERSION_CODE
}