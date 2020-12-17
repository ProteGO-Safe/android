package pl.gov.mc.protegosafe.data.db.realm

import io.realm.DynamicRealm
import io.realm.FieldAttribute
import io.realm.RealmMigration
import io.realm.RealmSchema

class RealmMigrations : RealmMigration {

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        var dbVersion = oldVersion
        if (dbVersion == REALM_VERSION_UP_TO_4_4) {
            addDistricts(realm.schema)
            dbVersion++
        }
        if (dbVersion == REALM_VERSION_UP_TO_4_7) {
            addCovidTest(realm.schema)
            dbVersion++
        }
        if (dbVersion == REALM_VERSION_UP_TO_4_9) {
            addActivities(realm.schema)
            dbVersion++
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

        realmSchema.create("SubscribedDistrictDto")
            ?.addField("id", Int::class.java, FieldAttribute.PRIMARY_KEY)
            ?.addField("updated", Long::class.java)
    }

    private fun addCovidTest(realmSchema: RealmSchema) {
        realmSchema.create("LatestProcessedDiagnosisKeyDto")
            ?.addField("id", Int::class.java, FieldAttribute.PRIMARY_KEY)
            ?.addField("timestamp", Long::class.java)

        realmSchema.create("TestSubscriptionDto")
            ?.addField("id", Int::class.java, FieldAttribute.PRIMARY_KEY)
            ?.addField("status", Int::class.java)
            ?.addField("accessToken", String::class.java, FieldAttribute.REQUIRED)
            ?.addField("guid", String::class.java, FieldAttribute.REQUIRED)
            ?.addField("updated", Long::class.java)

        realmSchema.create("TestSubscriptionPinDto")
            ?.addField("id", Int::class.java, FieldAttribute.PRIMARY_KEY)
            ?.addField("testPin", String::class.java, FieldAttribute.REQUIRED)
    }

    private fun addActivities(realmSchema: RealmSchema) {
        realmSchema.create("NotificationActivityDto")
            ?.addField(
                "id",
                String::class.java,
                FieldAttribute.PRIMARY_KEY,
                FieldAttribute.REQUIRED
            )
            ?.addField("title", String::class.java, FieldAttribute.REQUIRED)
            ?.addField("content", String::class.java, FieldAttribute.REQUIRED)
            ?.addField("timestamp", Long::class.java)

        realmSchema.create("ExposureCheckActivityDto")
            ?.addField(
                "id",
                String::class.java,
                FieldAttribute.PRIMARY_KEY,
                FieldAttribute.REQUIRED
            )
            ?.addField("riskLevel", Int::class.java)
            ?.addField("exposures", Int::class.java)
            ?.addField("timestamp", Long::class.java)

        realmSchema.create("PreAnalyzeDto")
            ?.addField(
                "token",
                String::class.java,
                FieldAttribute.PRIMARY_KEY,
                FieldAttribute.REQUIRED
            )
            ?.addField("keysCount", Long::class.java)
            ?.addField("timestamp", Long::class.java)

        realmSchema.create("RiskCheckActivityDto")
            ?.addField(
                "id",
                String::class.java,
                FieldAttribute.PRIMARY_KEY,
                FieldAttribute.REQUIRED
            )
            ?.addField("keys", Long::class.java)
            ?.addField("exposures", Int::class.java)
            ?.addField("timestamp", Long::class.java)

        realmSchema.create("CovidStatsDto")
            ?.addField("id", Int::class.java, FieldAttribute.PRIMARY_KEY)
            ?.addField("updated", Long::class.java)
            ?.addField("newCases", Long::class.java)
            ?.setRequired("newCases", false)
            ?.addField("totalCases", Long::class.java)
            ?.setRequired("totalCases", false)
            ?.addField("newDeaths", Long::class.java)
            ?.setRequired("newDeaths", false)
            ?.addField("totalDeaths", Long::class.java)
            ?.setRequired("totalDeaths", false)
            ?.addField("newRecovered", Long::class.java)
            ?.setRequired("newRecovered", false)
            ?.addField("totalRecovered", Long::class.java)
            ?.setRequired("totalRecovered", false)

        realmSchema.create("TotalKeysCountDto")
            ?.addField("id", Int::class.java, FieldAttribute.PRIMARY_KEY)
            ?.addField("totalKeysCount", Long::class.java)
            ?.addField("lastRiskCheckTimestamp", Long::class.java)
    }

    companion object {
        private const val REALM_VERSION_UP_TO_4_4 = 0L
        private const val REALM_VERSION_UP_TO_4_7 = 1L
        private const val REALM_VERSION_UP_TO_4_9 = 2L
    }
}
