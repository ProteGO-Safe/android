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
        Timber.i("Use insecure Realm DB")
        RealmConfiguration.Builder()
            .build()
            .apply {
                Realm.setDefaultConfiguration(this)
            }
    }
}