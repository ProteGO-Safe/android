package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Single

interface AppRepository {
    fun getVersionName(): Single<String>
}
