package pl.gov.anna.backend.api

import com.google.gson.annotations.SerializedName

class RegistrationRequest (
    @SerializedName("msisdn")
    val msisdn: String,
    standardRequestData: StandardRequestData
) : StandardRequestData(standardRequestData)

data class RegistrationResponse(
    @SerializedName("registration_id") val registrationId: String?,
    @SerializedName("code") val code: String?
)

class ConfirmRegistrationRequest(
    @SerializedName("registration_id")
    val registrationId: String,
    @SerializedName("code")
    val confirmationCode: String,
    standardRequestData: StandardRequestData
) : StandardRequestData(standardRequestData)

data class ConfirmationRegistrationResponse(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("error_msg")
    val errorMessage: String
)

open class StandardRequestData (
    @SerializedName("platform")
    val platform: String,

    @SerializedName("os_version")
    val osVersion: String,

    @SerializedName("device_name")
    val deviceName : String,

    @SerializedName("app_version")
    val appVersion : Int,

    @SerializedName("api_version")
    val apiVersion : Int,

    @SerializedName("user_id")
    val userId : String?,

    @SerializedName("lang")
    val lang : String
) {
    constructor(standardRequestData: StandardRequestData) :
            this(
                standardRequestData.platform,
                standardRequestData.osVersion,
                standardRequestData.deviceName,
                standardRequestData.appVersion,
                standardRequestData.apiVersion,
                standardRequestData.userId,
                standardRequestData.lang
            )
}