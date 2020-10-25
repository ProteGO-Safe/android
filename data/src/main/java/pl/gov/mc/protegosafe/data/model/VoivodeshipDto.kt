package pl.gov.mc.protegosafe.data.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class VoivodeshipDto(
    @PrimaryKey
    var id: Int = 0,
    var name: String = "",
    var districts: RealmList<DistrictDto> = RealmList(),
) : RealmObject()
