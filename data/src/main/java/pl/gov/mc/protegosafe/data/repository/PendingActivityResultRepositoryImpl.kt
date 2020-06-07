package pl.gov.mc.protegosafe.data.repository

import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.model.ActivityResult
import pl.gov.mc.protegosafe.domain.repository.PendingActivityResultRepository
import timber.log.Timber

class PendingActivityResultRepositoryImpl : PendingActivityResultRepository {
    private var activityResult: ActivityResult? = null

    override fun setActivityResult(result: ActivityResult): Completable = Completable.fromAction {
        activityResult = result
    }

    override fun popActivityResult(): Single<ActivityResult> {
        Timber.d("Get activity result $activityResult")
        return activityResult?.let {
            activityResult = null
            Single.fromCallable { it }
        }
            ?: Single.error(Exception("No pending activity result"))
    }

    override fun hasPendingActivityResult(): Single<Boolean> = Single.fromCallable {
        Timber.d("Has pending activity result: ${activityResult != null}")
        activityResult != null
    }
}
