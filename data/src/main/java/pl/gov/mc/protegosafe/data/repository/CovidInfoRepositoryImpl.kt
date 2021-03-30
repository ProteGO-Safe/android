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
import pl.gov.mc.protegosafe.data.extension.fromJson
import pl.gov.mc.protegosafe.data.extension.toJson
import pl.gov.mc.protegosafe.data.mapper.toDistrictDto
import pl.gov.mc.protegosafe.data.mapper.toEntity
import pl.gov.mc.protegosafe.data.model.SubscribedDistrictDto
import pl.gov.mc.protegosafe.data.model.TimestampsResponseData
import pl.gov.mc.protegosafe.data.model.TotalKeysCountDto
import pl.gov.mc.protegosafe.data.model.VoivodeshipsData
import pl.gov.mc.protegosafe.domain.extension.getCurrentTimeInSeconds
import pl.gov.mc.protegosafe.domain.model.DistrictItem
import pl.gov.mc.protegosafe.domain.model.ENStatsItem
import pl.gov.mc.protegosafe.domain.model.TimestampsItem
import pl.gov.mc.protegosafe.domain.model.VoivodeshipsItem
import pl.gov.mc.protegosafe.domain.repository.CovidInfoRepository
import pl.gov.mc.protegosafe.domain.repository.FileRepository
import timber.log.Timber
import kotlin.random.Random

// TODO: inject Gson
class CovidInfoRepositoryImpl(
    private val covidInfoService: CovidInfoService,
    private val covidInfoDao: CovidInfoDao,
    private val covidInfoDataStore: CovidInfoDataStore,
    private val activitiesDao: ActivitiesDao,
    private val fileRepository: FileRepository
) : CovidInfoRepository {

    override fun fetchTimestamps(): Single<TimestampsItem> {
        return covidInfoService
            .getTimestamps(Random.nextInt().toString())
            .map(TimestampsResponseData::toEntity)
    }

    override fun getTimestamps(): Single<TimestampsItem> {
        return fileRepository
            .readInternalFileOrEmpty(TIMESTAMPS_FILE_NAME)
            .map { it.fromJson<TimestampsItem>() }
            .onErrorReturn { TimestampsItem() }
    }

    override fun saveTimestamps(timestampsItem: TimestampsItem): Completable {
        return fileRepository.writeInternalFile(TIMESTAMPS_FILE_NAME, timestampsItem.toJson())
    }

    override fun getDashboardUpdateTimestamp(): Single<Long> {
        return Single.fromCallable {
            covidInfoDataStore.dashboardUpdateTimestamp
        }
    }

    override fun saveDashboardUpdateTimestamp(timestamp: Long): Completable {
        return Completable.fromAction {
            covidInfoDataStore.dashboardUpdateTimestamp = timestamp
        }
    }

    override fun fetchDashboard(): Single<String> {
        return covidInfoService.getDashboard(Random.nextInt().toString()).map { it.string() }
    }

    override fun getDashboard(): Single<String> {
        return fileRepository.readInternalFileOrEmpty(DASHBOARD_FILE_NAME)
    }

    override fun saveDashboard(dashboardJson: String): Completable {
        return fileRepository.writeInternalFile(DASHBOARD_FILE_NAME, dashboardJson)
    }

    override fun getDetailsUpdateTimestamp(): Single<Long> {
        return Single.fromCallable {
            covidInfoDataStore.detailsUpdateTimestamp
        }
    }

    override fun saveDetailsUpdateTimestamp(timestamp: Long): Completable {
        return Completable.fromAction {
            covidInfoDataStore.detailsUpdateTimestamp = timestamp
        }
    }

    override fun fetchDetails(): Single<String> {
        return covidInfoService.getDetails(Random.nextInt().toString()).map { it.string() }
    }

    override fun getDetails(): Single<String> {
        return fileRepository.readInternalFileOrEmpty(DETAILS_FILE_NAME)
    }

    override fun saveDetails(detailsJson: String): Completable {
        return fileRepository.writeInternalFile(DETAILS_FILE_NAME, detailsJson)
    }

    override fun getVoivodeshipsUpdateTimestamp(): Single<Long> {
        return Single.fromCallable {
            covidInfoDataStore.voivodeshipsUpdateTimestamp
        }
    }

    override fun saveVoivodeshipsUpdateTimestamp(timestamp: Long): Completable {
        return Completable.fromAction {
            covidInfoDataStore.voivodeshipsUpdateTimestamp = timestamp
        }
    }

    override fun fetchVoivodeships(): Single<String> {
        return covidInfoService.getVoivodeships(Random.nextInt().toString()).map { it.string() }
    }

    override fun getVoivodeships(): Single<VoivodeshipsItem> {
        return getVoivodeshipsJson()
            .map { it.fromJson<VoivodeshipsData>().toEntity() }
            .onErrorReturn { VoivodeshipsItem() }
    }

    override fun getVoivodeshipsJson(): Single<String> {
        return fileRepository.readInternalFileOrEmpty(VOIVODESHIPS_FILE_NAME)
    }

    override fun saveVoivodeships(voivodeshipsJson: String): Completable {
        return fileRepository.writeInternalFile(VOIVODESHIPS_FILE_NAME, voivodeshipsJson)
    }

    override fun syncDistrictsRestrictionsWithDb(districts: List<DistrictItem>): Completable {
        return covidInfoDao.upsertDistricts(districts.map(DistrictItem::toDistrictDto))
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

    override fun updateTotalKeysCount(keysCount: Long): Completable {
        return covidInfoDao.getTotalKeysCount()
            .onErrorReturn {
                if (it is NullPointerException) {
                    TotalKeysCountDto()
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
            covidInfoDao.getTotalKeysCount()
        ) { keysToday, keysLastWeek, totalKeysCount ->
            ENStatsItem(
                totalKeysCount.lastRiskCheckTimestamp,
                keysToday,
                keysLastWeek,
                totalKeysCount.totalKeysCount
            ).also {
                Timber.d("EN stats item = $it")
            }
        }
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

private const val TIMESTAMPS_FILE_NAME = "timestamps.json"
private const val DASHBOARD_FILE_NAME = "dashboard.json"
private const val DETAILS_FILE_NAME = "details.json"
private const val VOIVODESHIPS_FILE_NAME = "voivodeships.json"
