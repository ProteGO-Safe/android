package pl.gov.mc.protegosafe.data.db.dao

import doTransaction
import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.data.model.ExposureCheckActivityDto
import pl.gov.mc.protegosafe.data.model.RiskCheckActivityDto
import queryAllAsSingle

open class ActivitiesDao {
    fun addRiskCheckActivity(keysAmount: Long): Completable {
        return doTransaction {
            it.copyToRealmOrUpdate(RiskCheckActivityDto(keys = keysAmount))
        }
    }

    fun getRiskCheckActivities(): Single<List<RiskCheckActivityDto>> {
        return queryAllAsSingle()
    }

    fun addExposureCheckActivity(exposureCheckActivityDto: ExposureCheckActivityDto): Completable {
        return doTransaction {
            it.copyToRealmOrUpdate(exposureCheckActivityDto)
        }
    }

    fun getExposureCheckActivities(): Single<List<ExposureCheckActivityDto>> {
        return queryAllAsSingle()
    }
}
