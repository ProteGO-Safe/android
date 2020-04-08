package pl.gov.mc.protego.backend.api

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface StatusApi {

    @POST("get_status")
    fun getStatus(@Body getStatusRequest: GetStatusRequest): Single<GetStatusResponse>
}