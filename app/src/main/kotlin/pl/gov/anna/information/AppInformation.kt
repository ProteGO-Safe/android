package pl.gov.anna.information

import pl.gov.anna.BuildConfig

class AppInformation {
    val api: Int
        get() = BuildConfig.SUPPORTED_API_VERSION

    val version: Int
        get() = BuildConfig.VERSION_CODE
}