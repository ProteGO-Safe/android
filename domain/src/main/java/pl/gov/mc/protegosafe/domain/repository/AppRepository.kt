package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.util.Locale

interface AppRepository {
    fun getVersionName(): Single<String>

    /**
     * @return String in ISO 639-1 standard
     */
    fun getSystemLanguage(): Single<String>

    fun setAppLanguage(languageISO: String): Completable

    fun getLocale(): Locale
}
