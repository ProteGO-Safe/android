package pl.gov.mc.protego.backend.api

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface RegistrationAPI {

    @POST("register_device")
    fun register(@Body registrationRequest: RegistrationRequest): Single<RegistrationResponse>

    @POST("confirm_registration")
    fun confirmRegistration(@Body confirmRegistrationRequest: ConfirmRegistrationRequest): Single<ConfirmationRegistrationResponse>
}