package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.util.Date
import pl.gov.mc.protegosafe.domain.model.ExposureItem

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
}
