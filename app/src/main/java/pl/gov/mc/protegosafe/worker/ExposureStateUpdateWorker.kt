package pl.gov.mc.protegosafe.worker

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import io.reactivex.Completable
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject
import pl.gov.mc.protegosafe.Consts
import pl.gov.mc.protegosafe.domain.model.toExposureItem
import pl.gov.mc.protegosafe.domain.usecase.GetExposureInformationUseCase
import pl.gov.mc.protegosafe.domain.usecase.SaveMatchedTokenUseCase

class ExposureStateUpdateWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : RxWorker(appContext, workerParameters), KoinComponent {

    private val getExposureInformationUseCase: GetExposureInformationUseCase by inject()
    private val saveExposureUseCase: SaveMatchedTokenUseCase by inject()

    override fun createWork(): Single<Result> {
        val token = inputData.getString(Consts.EXPOSURE_STATE_UPDATED_EXTRA_TOKEN)
        checkNotNull(token)
        return getExposureInformationUseCase.execute(token)
            .map { listOfExposureInformation ->
                listOfExposureInformation.map { saveExposureUseCase.execute(it.toExposureItem()) }
            }
            .flatMapCompletable { listOfDbActions ->
                Completable.merge(listOfDbActions.asIterable())
            }
            .toSingleDefault(Result.success())
    }
}
