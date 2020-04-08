package pl.gov.mc.protego.backend.api

import io.reactivex.Single

class StatusService(
    private val statusApi: StatusApi,
    private val requestComposer: StatusRequestComposer
) {

    fun fetchStatusAndIds(date: String): Single<GetStatusResponse> {
        return Single.just(date)
            .map { requestComposer.status(date) }
            .flatMap { statusApi.getStatus(it) }
    }
}