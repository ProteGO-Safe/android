package pl.gov.mc.protegosafe.data.db.dao

import doTransaction
import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.data.model.LatestProcessedDiagnosisKeyDto
import singleQuery

class DiagnosisKeyDao {

    fun updateLatestProcessedDiagnosisKeyTimestamp(timestamp: Long): Completable {
        return doTransaction {
            it.copyToRealmOrUpdate(LatestProcessedDiagnosisKeyDto(timestamp = timestamp))
        }
    }

    fun getLatestProcessedDiagnosisKeyTimestamp(): Single<Long> {
        return singleQuery<LatestProcessedDiagnosisKeyDto>()
            .map {
                if (it.isEmpty()) {
                    LATEST_PROCESSED_DIAGNOSIS_KEY_TIMESTAMP_DEFAULT_VALUE
                } else {
                    it.first().timestamp
                }
            }
    }
}

private const val LATEST_PROCESSED_DIAGNOSIS_KEY_TIMESTAMP_DEFAULT_VALUE = 0L
