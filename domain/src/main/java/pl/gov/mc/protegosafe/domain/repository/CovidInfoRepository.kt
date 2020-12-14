package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.CovidInfoItem
import pl.gov.mc.protegosafe.domain.model.CovidStatsItem
import pl.gov.mc.protegosafe.domain.model.DistrictItem
import pl.gov.mc.protegosafe.domain.model.ENStatsItem
import pl.gov.mc.protegosafe.domain.model.VoivodeshipItem

interface CovidInfoRepository {
    fun getCovidInfo(): Single<CovidInfoItem>
    fun saveCovidInfoUpdateTimestamp(timestamp: Long): Completable
    fun getCovidInfoUpdateTimestamp(): Single<Long>
    fun syncDistrictsRestrictionsWithDb(voivodeships: List<VoivodeshipItem>): Completable
    fun getDistrictsRestrictions(): Single<List<VoivodeshipItem>>
    fun addDistrictToSubscribed(districtId: Int): Completable
    fun deleteDistrictFromSubscribed(districtId: Int): Completable
    fun getSortedSubscribedDistricts(): Single<List<DistrictItem>>
    fun updateCovidStats(covidStatsItem: CovidStatsItem): Completable
    fun getCovidStats(): Single<CovidStatsItem>
    fun updateTotalKeysCount(keysCount: Long): Completable
    fun getENStats(): Single<ENStatsItem>
}
