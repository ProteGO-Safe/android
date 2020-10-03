package pl.gov.mc.protegosafe.scheduler

import android.content.Context
import androidx.work.rxjava3.RxWorker
import androidx.work.WorkerParameters
import io.reactivex.rxjava3.core.Single
import org.koin.core.KoinComponent
import org.koin.core.inject
import pl.gov.mc.protegosafe.domain.extension.getExposureLastValidDate
import pl.gov.mc.protegosafe.domain.repository.ExposureRepository
import timber.log.Timber

class RemoveOldExposuresWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : RxWorker(appContext, workerParameters), KoinComponent {

    private val exposureRepository: ExposureRepository by inject()

    override fun createWork(): Single<Result> {
        Timber.i("RemoveOldExposuresWorker createWork")
        return exposureRepository.deleteBefore(getExposureLastValidDate())
            .andThen(Single.just(Result.success()))
            .onErrorResumeNext { Single.just(Result.retry()) }
    }
}
