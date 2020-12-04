package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import java.util.Date
import pl.gov.mc.protegosafe.domain.model.ExposureItem
import pl.gov.mc.protegosafe.domain.model.RiskLevelConfigurationItem
import pl.gov.mc.protegosafe.domain.model.RiskLevelItem

interface ExposureRepository {

    fun getAllResults(): Single<List<ExposureItem>>

    /**
     *  Inserts token into a database if do not already exist, or update
     */
    fun upsert(exposure: ExposureItem): Completable

    fun deleteByDate(vararg timestamps: Long): Completable

    /**
     * Delete [ExposureItem] with date before the specified [date].
     *
     * @param date [Date] before which ExposureItem items will be removed
     */
    fun deleteBefore(date: Date): Completable

    /**
     * Delete all records from token table
     */
    fun nukeDb(): Completable

    fun getRiskLevel(
        riskLevelConfigurationItem: RiskLevelConfigurationItem,
        exposure: ExposureItem
    ): Single<RiskLevelItem>

    fun calcRiskLevel(exposure: ExposureItem): Single<RiskLevelItem>

    fun getMaxExposureOrDefault(exposures: List<ExposureItem>): Single<ExposureItem>
}
