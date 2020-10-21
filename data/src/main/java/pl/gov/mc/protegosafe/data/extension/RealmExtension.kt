import android.os.HandlerThread
import android.os.Looper
import android.os.Process
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmQuery
import io.realm.Sort

/**
 * Copyright (C) 2019 Víctor Manuel Pineda Murcia
 * https://github.com/vicpinm/Kotlin-Realm-Extensions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

typealias Query<T> = RealmQuery<T>.() -> Unit

fun doTransaction(action: (Realm) -> Unit): Completable {
    return Completable.defer {
        Completable.fromCallable {
            Realm.getDefaultInstance().use { it ->
                it.executeTransaction(action::invoke)
            }
        }
    }
}

/**
 * Query for all items and listen to changes returning an Single.
 */
inline fun <reified T : RealmModel> queryAllAsSingle() = singleQuery<T>()

/**
 * INTERNAL FUNCTIONS
 */
@PublishedApi
internal inline fun <reified T : RealmModel> singleQuery(
    fieldName: List<String>? = null,
    order: List<Sort>? = null,
    noinline query: Query<T>? = null
) = performSingleQuery(fieldName, order, query, T::class.java)

private fun <T : RealmModel> T.singleQuery(
    fieldName: List<String>? = null,
    order: List<Sort>? = null,
    query: Query<T>? = null
) = performSingleQuery(fieldName, order, query, this.javaClass)

@PublishedApi
internal fun <T : RealmModel> performSingleQuery(
    fieldName: List<String>? = null,
    order: List<Sort>? = null,
    query: Query<T>? = null,
    javaClass: Class<T>
): Single<List<T>> {
    val looper = getLooper()
    return Single.create<List<T>> { emitter ->

        val realm = Realm.getDefaultInstance()
        val realmQuery: RealmQuery<T> = realm.where(javaClass)
        query?.invoke(realmQuery)

        val result = if (fieldName != null && order != null) {
            realmQuery.sort(fieldName.toTypedArray(), order.toTypedArray()).findAllAsync()
        } else {
            realmQuery.findAllAsync()
        }

        result.addChangeListener { it ->
            emitter.onSuccess(realm.copyFromRealm(it))
        }

        emitter.setDisposable(
            Disposables.fromAction {
                result.removeAllChangeListeners()
                realm.close()
                if (isRealmThread()) {
                    looper?.thread?.interrupt()
                }
            }
        )
    }.subscribeOn(AndroidSchedulers.from(looper))
        .unsubscribeOn(AndroidSchedulers.from(looper))
}

const val REALM_THREAD_NAME = "Scheduler-Realm-BackgroundThread"

fun isRealmThread() = Thread.currentThread().name == REALM_THREAD_NAME

internal fun getLooper(): Looper? {
    return if (Looper.myLooper() == null) {
        val backgroundThread = HandlerThread(
            REALM_THREAD_NAME,
            Process.THREAD_PRIORITY_BACKGROUND
        )
        backgroundThread.start()
        backgroundThread.looper
    } else {
        Looper.myLooper()
    }
}
