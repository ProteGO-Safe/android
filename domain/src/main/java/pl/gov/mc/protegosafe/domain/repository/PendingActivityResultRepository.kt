package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import pl.gov.mc.protegosafe.domain.model.ActivityResult

interface PendingActivityResultRepository {
    fun setActivityResult(result: ActivityResult): Completable
    fun popActivityResult(): Single<ActivityResult>
    fun hasPendingActivityResult(): Single<Boolean>
}
