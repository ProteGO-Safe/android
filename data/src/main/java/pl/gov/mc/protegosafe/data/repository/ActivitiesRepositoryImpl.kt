package pl.gov.mc.protegosafe.data.repository

import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.data.db.dao.ActivitiesDao
import pl.gov.mc.protegosafe.data.model.RiskCheckActivityDto
import pl.gov.mc.protegosafe.domain.repository.ActivitiesRepository

class ActivitiesRepositoryImpl(
    private val activitiesDao: ActivitiesDao
) : ActivitiesRepository {
    override fun saveRiskCheckActivity(keysAmount: Long): Completable {
        return activitiesDao.addRiskCheckActivity(keysAmount)
    }

    private fun getRiskCheckActivities(): Single<List<RiskCheckActivityDto>> {
        return activitiesDao.getRiskCheckActivities()
    }
}
