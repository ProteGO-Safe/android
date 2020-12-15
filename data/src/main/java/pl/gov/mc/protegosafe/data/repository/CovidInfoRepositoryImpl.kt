package pl.gov.mc.protegosafe.data.repository

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable
import org.threeten.bp.LocalDateTime
import org.threeten.bp.OffsetDateTime
import pl.gov.mc.protegosafe.data.cloud.CovidInfoService
import pl.gov.mc.protegosafe.data.db.CovidInfoDataStore
import pl.gov.mc.protegosafe.data.db.dao.ActivitiesDao
import pl.gov.mc.protegosafe.data.db.dao.CovidInfoDao
import pl.gov.mc.protegosafe.data.mapper.toCovidStatsDto
import pl.gov.mc.protegosafe.data.mapper.toEntity
import pl.gov.mc.protegosafe.data.mapper.toVoivodeshipDto
import pl.gov.mc.protegosafe.data.model.CovidStatsDto
import pl.gov.mc.protegosafe.data.model.TotalKeysCountDto
import pl.gov.mc.protegosafe.data.model.SubscribedDistrictDto
import pl.gov.mc.protegosafe.domain.model.CovidInfoItem
import pl.gov.mc.protegosafe.domain.extension.getCurrentTimeInSeconds
import pl.gov.mc.protegosafe.domain.model.CovidStatsItem
import pl.gov.mc.protegosafe.domain.model.DistrictItem
import pl.gov.mc.protegosafe.domain.model.ENStatsItem
import pl.gov.mc.protegosafe.domain.model.VoivodeshipItem
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository
import timber.log.Timber
import kotlin.random.Random

class CovidInfoRepositoryImpl(
    private val covidInfoService: CovidInfoService,
    private val covidInfoDao: CovidInfoDao,
    private val covidInfoDataStore: CovidInfoDataStore,
    private val activitiesDao: ActivitiesDao
) : CovidInfoRepository {

    override fun getCovidInfo(): Single<CovidInfoItem> {
        return covidInfoService.getCovidInfo(Random.nextInt().toString())
            .map { it.toEntity() }
    }

    override fun saveVoivodeshipsUpdateTimestamp(timestamp: Long): Completable {
        return Completable.fromAction {
            covidInfoDataStore.voivodeshipsUpdateTimestamp = timestamp
        }
    }

    override fun getVoivodeshipsUpdateTimestamp(): Single<Long> {
        return Single.fromCallable {
            covidInfoDataStore.voivodeshipsUpdateTimestamp
        }
    }

    override fun saveCovidStatsCheckTimestamp(timestamp: Long): Completable {
        return Completable.fromAction {
            covidInfoDataStore.covidStatsCheckTimestamp = timestamp
        }
    }

    override fun getCovidStatsCheckTimestamp(): Single<Long> {
        return Single.fromCallable {
            covidInfoDataStore.covidStatsCheckTimestamp
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

    override fun updateCovidStats(covidStatsItem: CovidStatsItem): Completable {
        Timber.d("Update covid stats: $covidStatsItem")
        return covidInfoDao.upsertCovidStats(covidStatsItem.toCovidStatsDto())
    }

    override fun getCovidStats(): Single<CovidStatsItem> {
        return covidInfoDao.getCovidStats()
            .onErrorReturn {
                if (it is NullPointerException) {
                    CovidStatsDto()
                } else {
                    throw it
                }
            }
            .map {
                it.toEntity()
            }
    }

    override fun updateTotalKeysCount(keysCount: Long): Completable {
        return covidInfoDao.getTotalKeysCount()
            .onErrorReturn {
                if (it is NullPointerException) {
                    TotalKeysCountDto(
                        totalKeysCount = keysCount
                    )
                } else {
                    throw it
                }
            }
            .flatMapCompletable {
                covidInfoDao.upsertTotalKeysCount(
                    it.apply {
                        totalKeysCount += keysCount
                        lastRiskCheckTimestamp = getCurrentTimeInSeconds()
                    }
                )
            }
    }

    override fun getENStats(): Single<ENStatsItem> {
        val localDate = LocalDateTime.now()
            .withHour(START_TIME_VALUE)
            .withMinute(START_TIME_VALUE)
            .withSecond(START_TIME_VALUE)
            .withNano(START_TIME_VALUE)

        return Single.zip(
            getKeysCountBeforeTimestamp(
                localDate.toInstant(OffsetDateTime.now().offset).epochSecond
            ),
            getKeysCountBeforeTimestamp(
                localDate.minusDays(LAST_DAYS_VALUE)
                    .toInstant(OffsetDateTime.now().offset).epochSecond
            ),
            covidInfoDao.getTotalKeysCount(),
            { keysToday, keysLastWeek, totalKeysCount ->
                ENStatsItem(
                    totalKeysCount.lastRiskCheckTimestamp,
                    keysToday,
                    keysLastWeek,
                    totalKeysCount.totalKeysCount
                ).also {
                    Timber.d("EN stats item = $it")
                }
            }
        )
    }

    private fun getKeysCountBeforeTimestamp(timestamp: Long): Single<Long> {
        return activitiesDao.getPreAnalyzes()
            .map { preAnalyzes ->
                var totalCount = 0L
                preAnalyzes.filter {
                    it.timestamp > timestamp
                }.forEach {
                    totalCount += it.keysCount
                }
                totalCount
            }
    }
}

private const val START_TIME_VALUE = 0
private const val LAST_DAYS_VALUE = 7L
