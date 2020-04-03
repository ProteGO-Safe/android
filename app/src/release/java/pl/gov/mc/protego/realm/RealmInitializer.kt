package pl.gov.mc.protego.realm

import android.content.Context
import io.realm.Realm
import io.realm.RealmConfiguration
import timber.log.Timber

class RealmInitializer(
    private val realmEncryption: RealmEncryption
) {

    fun setup(context: Context) {
        Realm.init(context)
        Timber.i("Use secure Realm DB")
        val realmKey = realmEncryption.generateOrGetRealmEncryptionKey(context)
        RealmConfiguration.Builder()
            .encryptionKey(realmKey)
            .build()
            .apply {
                Realm.setDefaultConfiguration(this)
            }
    }
}