package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.model.DistrictItem
import pl.gov.mc.protegosafe.domain.model.ENStatsItem
import pl.gov.mc.protegosafe.domain.model.TimestampsItem
import pl.gov.mc.protegosafe.domain.model.VoivodeshipsItem

interface CovidInfoRepository {
    fun fetchTimestamps(): Single<TimestampsItem>
    fun getTimestamps(): Single<TimestampsItem>
    fun saveTimestamps(timestampsItem: TimestampsItem): Completable

    fun getDashboardUpdateTimestamp(): Single<Long>
    fun saveDashboardUpdateTimestamp(timestamp: Long): Completable
    fun fetchDashboard(): Single<String>
    fun getDashboard(): Single<String>
    fun saveDashboard(dashboardJson: String): Completable

    fun getDetailsUpdateTimestamp(): Single<Long>
    fun saveDetailsUpdateTimestamp(timestamp: Long): Completable
    fun fetchDetails(): Single<String>
    fun getDetails(): Single<String>
    fun saveDetails(detailsJson: String): Completable

    fun getVoivodeshipsUpdateTimestamp(): Single<Long>
    fun saveVoivodeshipsUpdateTimestamp(timestamp: Long): Completable
    fun fetchVoivodeships(): Single<String>
    fun getVoivodeships(): Single<VoivodeshipsItem>
    fun getVoivodeshipsJson(): Single<String>
    fun saveVoivodeships(voivodeshipsJson: String): Completable

    fun syncDistrictsRestrictionsWithDb(districts: List<DistrictItem>): Completable
    fun addDistrictToSubscribed(districtId: Int): Completable
    fun deleteDistrictFromSubscribed(districtId: Int): Completable
    fun getSortedSubscribedDistricts(): Single<List<DistrictItem>>

    fun updateTotalKeysCount(keysCount: Long): Completable

    fun getENStats(): Single<ENStatsItem>
}
