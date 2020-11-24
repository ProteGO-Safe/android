package pl.gov.mc.protegosafe.helpers

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.data.db.WebViewLoggingDataStore
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread

class SetWebViewLoggingEnabledUseCase(
    private val webViewLoggingDataStore: WebViewLoggingDataStore,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(isEnabled: Boolean): Single<String> {
        return Completable.fromAction {
            webViewLoggingDataStore.isLoggingEnabled = isEnabled
        }.toSingle {
            if (isEnabled) {
                "Web view logging enabled"
            } else {
                "Web view logging disabled"
            }
        }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}
