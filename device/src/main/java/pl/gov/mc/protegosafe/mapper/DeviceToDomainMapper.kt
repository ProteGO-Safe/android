package pl.gov.mc.protegosafe.mapper

import com.google.android.gms.tasks.Task
import com.google.firebase.functions.HttpsCallableResult
import io.bluetrace.opentrace.idmanager.TemporaryID
import io.reactivex.Completable
import pl.gov.mc.protegosafe.domain.model.TemporaryIDItem

fun TemporaryID.toDomainModel() = TemporaryIDItem(
    startTime = startTime,
    tempID = tempID,
    expiryTime = expiryTime
)

fun Task<HttpsCallableResult>.toCompletable() = Completable.create { emitter ->
    this.addOnCompleteListener {
        if (it.isSuccessful) emitter.onComplete() else emitter.onError(it.exception as Throwable)
    }
}