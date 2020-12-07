package pl.gov.mc.protegosafe.data.db.dao

import doTransaction
import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.data.model.ExposureCheckActivityDto
import pl.gov.mc.protegosafe.data.model.PreAnalyzeDto
import pl.gov.mc.protegosafe.data.model.RiskCheckActivityDto
import queryAllAsSingle
import timber.log.Timber

open class ActivitiesDao {
    fun saveRiskCheckActivity(riskCheckActivityDto: RiskCheckActivityDto): Completable {
        Timber.d("Saving risk check activity $riskCheckActivityDto")
        return doTransaction {
            it.copyToRealmOrUpdate(riskCheckActivityDto)
        }
    }

    fun getRiskCheckActivities(): Single<List<RiskCheckActivityDto>> {
        return queryAllAsSingle()
    }

    fun saveExposureCheckActivity(exposureCheckActivityDto: ExposureCheckActivityDto): Completable {
        Timber.d("Saving exposure check activity $exposureCheckActivityDto")
        return doTransaction {
            it.copyToRealmOrUpdate(exposureCheckActivityDto)
        }
    }

    fun getExposureCheckActivities(): Single<List<ExposureCheckActivityDto>> {
        return queryAllAsSingle()
    }

    fun savePreAnalyze(preAnalyzeDto: PreAnalyzeDto): Completable {
        return doTransaction {
            it.copyToRealmOrUpdate(preAnalyzeDto)
        }
    }

    fun getKeysCountForToken(token: String): Single<Long> {
        return queryAllAsSingle<PreAnalyzeDto>()
            .map { preAnalyzes ->
                preAnalyzes.firstOrNull {
                    it.token == token
                }?.keysCount
            }
    }
}
