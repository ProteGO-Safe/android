package pl.gov.mc.protegosafe.data.db.dao

import doTransaction
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.util.Date
import pl.gov.mc.protegosafe.data.model.ExposureDto
import queryAllAsSingle

class ExposureDao {
    fun getAllResults(): Single<List<ExposureDto>> {
        return queryAllAsSingle()
    }

    fun upsert(entity: ExposureDto): Completable {
        return doTransaction {
            it.copyToRealmOrUpdate(entity)
        }
    }

    fun deleteByDate(vararg timestamps: Long): Completable {
        return doTransaction {
            it.where(ExposureDto::class.java).findAll()?.filter { item ->
                item.date in timestamps
            }?.forEach { item ->
                item.deleteFromRealm()
            }
        }
    }

    fun deleteBefore(date: Date): Completable {
        return doTransaction {
            it.where(ExposureDto::class.java).findAll()?.filter { item ->
                Date(item.date).before(date)
            }?.forEach { item ->
                item.deleteFromRealm()
            }
        }
    }

    fun nukeDb(): Completable {
        return doTransaction {
            it.where(ExposureDto::class.java).findAll()?.forEach { item ->
                item.deleteFromRealm()
            }
        }
    }
}
