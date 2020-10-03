package pl.gov.mc.protegosafe.data.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.util.Date
import pl.gov.mc.protegosafe.data.db.dao.ExposureDao
import pl.gov.mc.protegosafe.data.mapper.toEntity
import pl.gov.mc.protegosafe.data.mapper.toExposureDto
import pl.gov.mc.protegosafe.data.model.ExposureDto
import pl.gov.mc.protegosafe.domain.model.ExposureItem
import pl.gov.mc.protegosafe.domain.repository.ExposureRepository

class ExposureRepositoryImpl(
    private val exposureDao: ExposureDao
) : ExposureRepository {
    override fun getAllResults(): Single<List<ExposureItem>> {
        return exposureDao.getAllResults().map { listOfExposureDtos: List<ExposureDto> ->
            listOfExposureDtos.map { it.toEntity() }
        }
    }

    override fun upsert(exposure: ExposureItem): Completable {
        return exposureDao.upsert(exposure.toExposureDto())
    }

    override fun deleteByDate(vararg timestamps: Long): Completable {
        return exposureDao.deleteByDate(*timestamps)
    }

    override fun deleteBefore(date: Date): Completable {
        return exposureDao.deleteBefore(date)
    }

    override fun nukeDb(): Completable {
        return exposureDao.nukeDb()
    }
}
