package pl.gov.mc.protegosafe.worker

import android.content.Context
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import io.reactivex.Completable
import io.reactivex.Single
import org.koin.core.KoinComponent
import org.koin.core.inject
import pl.gov.mc.protegosafe.Consts
import pl.gov.mc.protegosafe.domain.model.ExposureItem
import pl.gov.mc.protegosafe.domain.model.toExposureItem
import pl.gov.mc.protegosafe.domain.usecase.GetExposureInformationUseCase
import pl.gov.mc.protegosafe.domain.usecase.SaveExposureUseCase
import pl.gov.mc.protegosafe.domain.usecase.SaveExposureCheckActivityUseCase
import pl.gov.mc.protegosafe.domain.usecase.SaveRiskCheckActivityUseCase

class ExposureStateUpdateWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : RxWorker(appContext, workerParameters), KoinComponent {

    private val getExposureInformationUseCase: GetExposureInformationUseCase by inject()
    private val saveExposureUseCase: SaveExposureUseCase by inject()
    private val saveExposureCheckActivityUseCase: SaveExposureCheckActivityUseCase by inject()
    private val saveRiskCheckActivityUseCase: SaveRiskCheckActivityUseCase by inject()

    override fun createWork(): Single<Result> {
        val token = inputData.getString(Consts.EXPOSURE_STATE_UPDATED_EXTRA_TOKEN)
        checkNotNull(token)
        return getExposureInformationUseCase.execute(token)
            .map { listOfExposureInformation ->
                listOfExposureInformation.map { it.toExposureItem() }
            }.flatMapCompletable { listOfExposureItems ->
                saveActivities(token, listOfExposureItems)
                    .andThen(saveExposures(listOfExposureItems))
            }.toSingleDefault(Result.success())
    }

    private fun saveExposures(exposures: List<ExposureItem>): Completable {
        return Completable.merge(
            exposures.map { saveExposureUseCase.execute(it) }
                .asIterable()
        )
    }

    private fun saveActivities(
        token: String,
        listOfExposureItems: List<ExposureItem>
    ): Completable {
        return saveRiskCheckActivityUseCase.execute(token, listOfExposureItems.size)
            .andThen(
                Completable.defer {
                    saveExposureCheckActivityUseCase.execute(
                        listOfExposureItems
                    )
                }
            )
    }
}
