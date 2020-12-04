package pl.gov.mc.protegosafe.data.repository

import io.reactivex.Completable
import io.reactivex.Single
import java.util.Date
import pl.gov.mc.protegosafe.data.db.dao.ExposureDao
import pl.gov.mc.protegosafe.data.mapper.toEntity
import pl.gov.mc.protegosafe.data.mapper.toExposureDto
import pl.gov.mc.protegosafe.data.model.ExposureDto
import pl.gov.mc.protegosafe.data.model.RiskLevelData
import pl.gov.mc.protegosafe.domain.extension.getExposureLastValidDate
import pl.gov.mc.protegosafe.domain.model.ExposureItem
import pl.gov.mc.protegosafe.domain.model.RiskLevelConfigurationItem
import pl.gov.mc.protegosafe.domain.model.RiskLevelItem
import pl.gov.mc.protegosafe.domain.repository.ExposureRepository
import pl.gov.mc.protegosafe.domain.repository.RemoteConfigurationRepository

class ExposureRepositoryImpl(
    private val exposureDao: ExposureDao,
    private val remoteConfigurationRepository: RemoteConfigurationRepository
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

    override fun getRiskLevel(
        riskLevelConfigurationItem: RiskLevelConfigurationItem,
        exposure: ExposureItem
    ): Single<RiskLevelItem> {
        return Single.fromCallable {
            RiskLevelData.fromRiskScore(
                riskLevelConfigurationItem,
                exposure.riskScore
            ).toEntity()
        }
    }

    override fun calcRiskLevel(exposure: ExposureItem): Single<RiskLevelItem> {
        return getRiskLevelConfiguration()
            .flatMap {
                getRiskLevel(it, exposure)
            }
    }

    private fun getRiskLevelConfiguration(): Single<RiskLevelConfigurationItem> {
        return remoteConfigurationRepository.update()
            .andThen(remoteConfigurationRepository.getRiskLevelConfiguration())
    }

    override fun getMaxExposureOrDefault(exposures: List<ExposureItem>): Single<ExposureItem> {
        return Single.just(
            exposures.filter { !Date(it.date).before(getExposureLastValidDate()) }
                .maxByOrNull { it.riskScore }
                ?: ExposureItem(
                    System.currentTimeMillis(),
                    NO_EXPOSURE_DEFAULT_VALUE,
                    NO_EXPOSURE_DEFAULT_VALUE
                )
        )
    }
}

private const val NO_EXPOSURE_DEFAULT_VALUE = 0
