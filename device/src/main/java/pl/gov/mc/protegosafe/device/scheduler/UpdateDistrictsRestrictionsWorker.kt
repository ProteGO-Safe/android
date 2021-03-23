package pl.gov.mc.protegosafe.device.scheduler

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject
import pl.gov.mc.protegosafe.domain.usecase.restrictions.UpdateVoivodeshipsIfRequiredUseCase
import timber.log.Timber

class UpdateDistrictsRestrictionsWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : RxWorker(appContext, workerParameters), KoinComponent {

    private val updateVoivodeshipsIfRequiredUseCase: UpdateVoivodeshipsIfRequiredUseCase by inject()

    override fun createWork(): Single<Result> {
        return updateVoivodeshipsIfRequiredUseCase.execute()
            .andThen(Single.just(Result.success()))
            .onErrorResumeNext {
                Timber.d(it)
                Single.just(Result.success())
            }
    }
}
