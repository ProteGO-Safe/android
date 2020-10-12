package pl.gov.mc.protegosafe.data.db.realm

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration
import io.realm.RealmSchema

class RealmMigrations : RealmMigration {

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        when (oldVersion) {
            REALM_VERSION_UP_TO_4_4 -> {
                addDistricts(realm.schema)
            }
        }
    }

    private fun addDistricts(realmSchema: RealmSchema) {
        realmSchema.create("DistrictDto")
            ?.addField("id", Int::class.java, FieldAttribute.PRIMARY_KEY)
            ?.addField("name", String::class.java, FieldAttribute.REQUIRED)
            ?.addField("state", Int::class.java)

        realmSchema.create("VoivodeshipDto")
            ?.addField("id", Int::class.java, FieldAttribute.PRIMARY_KEY)
            ?.addField("name", String::class.java, FieldAttribute.REQUIRED)
            ?.addRealmListField("districts", realmSchema.get("DistrictDto")!!)
    }

    companion object {
        private const val REALM_VERSION_UP_TO_4_4 = 0L
    }
}
