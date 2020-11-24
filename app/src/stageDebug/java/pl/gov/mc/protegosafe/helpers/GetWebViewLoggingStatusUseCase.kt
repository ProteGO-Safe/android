package pl.gov.mc.protegosafe.helpers

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.data.db.WebViewLoggingDataStore
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread

class GetWebViewLoggingStatusUseCase(
    private val webViewLoggingDataStore: WebViewLoggingDataStore,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(): Single<Boolean> {
        return Single.fromCallable {
            webViewLoggingDataStore.isLoggingEnabled
        }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }
}
