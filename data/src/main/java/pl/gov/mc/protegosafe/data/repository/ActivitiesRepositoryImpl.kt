package pl.gov.mc.protegosafe.data.repository

import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.data.db.dao.ActivitiesDao
import pl.gov.mc.protegosafe.data.mapper.toExposureCheckActivityDto
import pl.gov.mc.protegosafe.data.model.ExposureCheckActivityDto
import pl.gov.mc.protegosafe.data.model.RiskCheckActivityDto
import pl.gov.mc.protegosafe.domain.model.ExposureCheckActivityItem
import pl.gov.mc.protegosafe.domain.repository.ActivitiesRepository
import timber.log.Timber

class ActivitiesRepositoryImpl(
    private val activitiesDao: ActivitiesDao
) : ActivitiesRepository {
    override fun saveRiskCheckActivity(keysAmount: Long): Completable {
        return activitiesDao.addRiskCheckActivity(keysAmount)
    }

    private fun getRiskCheckActivities(): Single<List<RiskCheckActivityDto>> {
        return activitiesDao.getRiskCheckActivities()
    }

    override fun saveExposureCheckActivity(
        exposureCheckActivityItem: ExposureCheckActivityItem
    ): Completable {
        Timber.d("Save Exposure Check Activity $exposureCheckActivityItem")
        return activitiesDao.addExposureCheckActivity(
            exposureCheckActivityItem.toExposureCheckActivityDto()
        )
    }

    private fun getExposureCheckActivities(): Single<List<ExposureCheckActivityDto>> {
        return activitiesDao.getExposureCheckActivities()
    }
}
