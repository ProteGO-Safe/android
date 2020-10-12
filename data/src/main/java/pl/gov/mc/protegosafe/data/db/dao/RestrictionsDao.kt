package pl.gov.mc.protegosafe.data.db.dao

import doTransaction
import io.reactivex.Completable
import io.reactivex.Single
import pl.gov.mc.protegosafe.data.model.DistrictDto
import pl.gov.mc.protegosafe.data.model.VoivodeshipDto
import queryAllAsSingle
import singleQuery

open class RestrictionsDao {

    fun getAllVoivodeshipsRestrictions(): Single<List<VoivodeshipDto>> {
        return queryAllAsSingle()
    }

    fun getDistrictById(id: Int): Single<DistrictDto> {
        return singleQuery<DistrictDto>()
            .map { districtsList ->
                districtsList.first { it.id == id }
            }
    }

    fun upsertVoivodeships(voivodeships: List<VoivodeshipDto>): Completable {
        return doTransaction {
            it.copyToRealmOrUpdate(voivodeships)
        }
    }

    fun upsertDistricts(districts: List<DistrictDto>): Completable {
        return doTransaction {
            it.copyToRealmOrUpdate(districts)
        }
    }

    fun nukeDb(): Completable {
        return doTransaction {
            listOf(VoivodeshipDto::class.java, DistrictDto::class.java).forEach { realmObject ->
                it.where(realmObject).findAll()?.forEach { item ->
                    item.deleteFromRealm()
                }
            }
        }
    }
}
