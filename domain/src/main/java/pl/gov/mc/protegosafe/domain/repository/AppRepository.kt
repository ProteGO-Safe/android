package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Single

interface AppRepository {
    fun getVersionName(): Single<String>

    /**
     * @return String in ISO 639-1 standard
     */
    fun getSystemLanguage(): Single<String>
}
