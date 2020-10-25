package pl.gov.mc.protegosafe.scheduler

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject
import pl.gov.mc.protegosafe.domain.usecase.restrictions.UpdateDistrictsRestrictionsUseCase
import timber.log.Timber

class UpdateDistrictsRestrictionsWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : RxWorker(appContext, workerParameters), KoinComponent {

    private val updateDistrictsRestrictionsUseCase: UpdateDistrictsRestrictionsUseCase by inject()

    override fun createWork(): Single<Result> {
        return updateDistrictsRestrictionsUseCase.execute()
            .andThen(Single.just(Result.success()))
            .onErrorResumeNext {
                Timber.e(it)
                Single.just(Result.retry())
            }
    }
}
