package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable

interface ActivitiesRepository {
    fun saveRiskCheckActivity(keysAmount: Long): Completable
}
