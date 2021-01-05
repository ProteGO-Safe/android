package pl.gov.mc.protegosafe.extension

import com.google.android.play.core.tasks.Task
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber

fun Task<*>.toCompletable() = Completable.create { emitter ->
    this.addOnCompleteListener { task ->
        if (!emitter.isDisposed) {
            if (task.isSuccessful) {
                emitter.onComplete()
            } else {
                emitter.onError(task.exception as Throwable)
            }
        } else {
            Timber.w("Task<*>.toCompletable() emitter disposed")
        }
    }
}

fun <T> Task<T>.toSingle(): Single<T> {
    return Single.create { emitter ->
        this.addOnCompleteListener { task ->
            if (!emitter.isDisposed) {
                if (task.isSuccessful) {
                    emitter.onSuccess(task.result)
                } else {
                    emitter.onError(task.exception as Throwable)
                }
            } else {
                Timber.w("Task<T>.toSingle() emitter disposed")
            }
        }
    }
}
