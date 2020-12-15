package pl.gov.mc.protegosafe.device.scheduler

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import io.reactivex.Completable
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository
import pl.gov.mc.protegosafe.domain.usecase.UpdateCovidStatsUseCase
import pl.gov.mc.protegosafe.domain.usecase.restrictions.UpdateDistrictsRestrictionsUseCase
import timber.log.Timber

class UpdateCovidInfoWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : RxWorker(appContext, workerParameters), KoinComponent {

    private val covidInfoRepository: CovidInfoRepository by inject()
    private val updateDistrictsRestrictionsUseCase: UpdateDistrictsRestrictionsUseCase by inject()
    private val updateCovidStatsUseCase: UpdateCovidStatsUseCase by inject()

    override fun createWork(): Single<Result> {
        return covidInfoRepository.getCovidInfo()
            .flatMapCompletable {
                updateDistrictsRestrictionsUseCase.execute(it)
                    .andThen(
                        Completable.defer {
                            updateCovidStatsUseCase.execute(it)
                        }
                    )
            }
            .andThen(Single.just(Result.success()))
            .onErrorResumeNext {
                Timber.e(it)
                Single.just(Result.retry())
            }
    }
}
