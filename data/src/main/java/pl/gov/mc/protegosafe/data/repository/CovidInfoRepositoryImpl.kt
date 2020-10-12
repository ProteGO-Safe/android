package pl.gov.mc.protegosafe.data.repository

import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.data.cloud.CovidInfoService
import pl.gov.mc.protegosafe.data.db.CovidInfoDataStore
import pl.gov.mc.protegosafe.data.db.dao.RestrictionsDao
import pl.gov.mc.protegosafe.data.mapper.toEntity
import pl.gov.mc.protegosafe.data.mapper.toVoivodeshipDto
import pl.gov.mc.protegosafe.domain.CovidInfoItem
import pl.gov.mc.protegosafe.domain.model.VoivodeshipItem
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository

class CovidInfoRepositoryImpl(
    private val covidInfoService: CovidInfoService,
    private val restrictionsDao: RestrictionsDao,
    private val covidInfoDataStore: CovidInfoDataStore
) : CovidInfoRepository {

    override fun getCovidInfo(): Single<CovidInfoItem> {
        return covidInfoService.getCovidInfo()
            .map { it.toEntity() }
    }

    override fun saveCovidInfoUpdateTimestamp(timestamp: Long): Completable {
        return Completable.fromAction {
            covidInfoDataStore.updateTimestamp = timestamp
        }
    }

    override fun getCovidInfoUpdateTimestamp(): Single<Long> {
        return Single.fromCallable {
            covidInfoDataStore.updateTimestamp
        }
    }

    override fun syncDistrictsRestrictionsWithDb(voivodeships: List<VoivodeshipItem>): Completable {
        return restrictionsDao.upsertVoivodeships(
            voivodeships.map { it.toVoivodeshipDto() }
        )
    }

    override fun getDistrictsRestrictions(): Single<List<VoivodeshipItem>> {
        return restrictionsDao.getAllVoivodeshipsRestrictions()
            .map { list ->
                list.map { it.toEntity() }
            }
    }
}
