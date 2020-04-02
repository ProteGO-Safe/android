package pl.gov.mc.protego.ui.main

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.gov.mc.protego.R
import pl.gov.mc.protego.information.Session
import pl.gov.mc.protego.ui.base.BaseActivity
import pl.gov.mc.protego.ui.registration.RegistrationActivity
import io.realm.RealmConfiguration
import android.widget.Toast
import io.realm.Realm

import io.realm.RealmObject
import pl.gov.mc.protego.realm.RealmEncryption

import timber.log.Timber





class MainActivity : BaseActivity() {

    private val viewModel: MainActivityViewModel by viewModel()
    private val session: Session by inject()
    private val realmEncryption: RealmEncryption by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logout_button.setOnClickListener {
            session.logout()
            startActivity(Intent(this, RegistrationActivity::class.java))
            finish()
        }
        Realm.init(this)
//        store.unlockKeyStore(1)

//        realmEncryption.reset(this)
        val realmKey = realmEncryption.generateOrGetRealmEncryptionKey(this)

        val realmConfig =
//            RealmConfiguration.Builder().build()
            RealmConfiguration.Builder().encryptionKey(realmKey).build()
        Realm.setDefaultConfiguration(realmConfig)

        val realm = getRealm()!!
//        realm.beginTransaction()
//        val todoItem = realm.createObject(TodoItem::class.java)
//        todoItem.name ="Item "
//        realm.commitTransaction()

        realm.beginTransaction()
        val todos = realm.where(TodoItem::class.java).count()
        Timber.d("Quantity: ${todos}")
//        todoItem.name ="Item "
        realm.commitTransaction()


    }

    override fun onResume() {
        super.onResume()
//        viewModel.onResume()
    }

    private fun getRealm(): Realm? {
        try {
            return Realm.getDefaultInstance()
        } catch (e: IllegalArgumentException) {
            Timber.e(e, "Cannot open realm")
            Toast.makeText(this, "Please unlock Realm first.", Toast.LENGTH_SHORT).show()
            return null
        }

    }

}

open class TodoItem : RealmObject() {
    var name: String? = null
    var isDone: Boolean = false
}
