package pl.gov.mc.protegosafe.mapper

import com.google.android.gms.tasks.Task
import com.google.firebase.functions.HttpsCallableResult
import io.bluetrace.opentrace.idmanager.TemporaryID
import io.reactivex.Completable
import pl.gov.mc.protegosafe.domain.model.TemporaryIDItem
import pl.gov.mc.protegosafe.domain.model.TraceStatusItem
import pl.gov.mc.protegosafe.model.TraceStatusDto

fun TemporaryID.toDomainModel() = TemporaryIDItem(
    startTime = startTime,
    tempID = tempID,
    expiryTime = expiryTime
)

fun Task<HttpsCallableResult>.toCompletable() = Completable.create { emitter ->
    this.addOnCompleteListener {
        if (!emitter.isDisposed) {
            if (it.isSuccessful) emitter.onComplete() else emitter.onError(it.exception as Throwable)
        }
    }
}

fun TraceStatusDto.toDomainItem() = TraceStatusItem(enableBtService = enableBtService)