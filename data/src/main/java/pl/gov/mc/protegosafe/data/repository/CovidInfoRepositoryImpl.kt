package pl.gov.mc.protegosafe.data.repository

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import pl.gov.mc.protegosafe.data.cloud.CovidInfoService
import pl.gov.mc.protegosafe.data.db.CovidInfoDataStore
import pl.gov.mc.protegosafe.data.db.dao.CovidInfoDao
import pl.gov.mc.protegosafe.data.mapper.toEntity
import pl.gov.mc.protegosafe.data.mapper.toVoivodeshipDto
import pl.gov.mc.protegosafe.data.model.SubscribedDistrictDto
import pl.gov.mc.protegosafe.domain.CovidInfoItem
import pl.gov.mc.protegosafe.domain.model.DistrictItem
import pl.gov.mc.protegosafe.domain.model.VoivodeshipItem
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository
import kotlin.random.Random

class CovidInfoRepositoryImpl(
    private val covidInfoService: CovidInfoService,
    private val covidInfoDao: CovidInfoDao,
    private val covidInfoDataStore: CovidInfoDataStore
) : CovidInfoRepository {

    override fun getCovidInfo(): Single<CovidInfoItem> {
        return covidInfoService.getCovidInfo(Random.nextInt().toString())
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
        return covidInfoDao.upsertVoivodeships(
            voivodeships.map { it.toVoivodeshipDto() }
        )
    }

    override fun getDistrictsRestrictions(): Single<List<VoivodeshipItem>> {
        return covidInfoDao.getAllVoivodeshipsRestrictions()
            .map { list ->
                list.map { it.toEntity() }
            }
    }

    override fun addDistrictToSubscribed(districtId: Int): Completable {
        return covidInfoDao.addToSubscribedDistricts(SubscribedDistrictDto(districtId))
    }

    override fun deleteDistrictFromSubscribed(districtId: Int): Completable {
        return covidInfoDao.deleteDistrictFromSubscribed(districtId)
    }

    override fun getSortedSubscribedDistricts(): Single<List<DistrictItem>> {
        return covidInfoDao.getSubscribedDistrictsIds()
            .flatMapObservable { subscribedDistrictsList ->
                subscribedDistrictsList.sortedBy { it.updated }
                    .toObservable()
            }.flatMapSingle {
                covidInfoDao.getDistrictById(it.id)
            }
            .map { it.toEntity() }
            .toList()
    }
}
