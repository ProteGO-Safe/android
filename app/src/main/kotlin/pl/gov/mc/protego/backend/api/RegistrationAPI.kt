package pl.gov.mc.protego.backend.api

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface RegistrationAPI {

    @POST("register_no_msisdn")
    fun registerAnonymously(@Body anonymousRegistrationRequest: AnonymousRegistrationRequest): Single<AnonymousRegistrationResponse>

    @POST("register")
    fun register(@Body registrationRequest: RegistrationRequest): Single<RegistrationResponse>

    @POST("confirm_registration")
    fun confirmRegistration(@Body confirmRegistrationRequest: ConfirmRegistrationRequest): Single<ConfirmationRegistrationResponse>
}