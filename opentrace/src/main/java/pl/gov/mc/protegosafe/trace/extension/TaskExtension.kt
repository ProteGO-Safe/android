package pl.gov.mc.protegosafe.trace.extension

import com.google.android.gms.tasks.Task
import com.google.firebase.functions.HttpsCallableResult
import io.reactivex.Completable

fun Task<HttpsCallableResult>.toCompletable() = Completable.create { emitter ->
    this.addOnCompleteListener {
        if (!emitter.isDisposed) {
            if (it.isSuccessful) emitter.onComplete() else emitter.onError(it.exception as Throwable)
        }
    }
}