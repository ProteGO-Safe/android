package pl.gov.mc.protegosafe.model

import com.google.gson.annotations.SerializedName

/**
 * A statement returned by the Attestation API.
 */
data class AttestationData(

    /**
     * Embedded nonce sent as part of the request.
     */
    @SerializedName("nonce")
    val nonce: String? = null,

    /**
     * Timestamp of the request.
     */
    @SerializedName("timestampMs")
    val timestampMs: Long = 0,

    /**
     * Package name of the APK that submitted this request.
     */
    @SerializedName("apkPackageName")
    val apkPackageName: String? = null,

    /**
     * Digest of the APK that submitted this request.
     */
    @SerializedName("apkDigestSha256")
    val apkDigestSha256: String? = null,

    /**
     * The device passed CTS and matches a known profile.
     */
    @SerializedName("ctsProfileMatch")
    val ctsProfileMatch: Boolean = false,

    /**
     * The device has passed a basic integrity test, but the CTS profile could not be verified.
     */
    @SerializedName("basicIntegrity")
    val basicIntegrity: Boolean = false,

    /**
     * Advice how user can fix phone integrity
     */
    @SerializedName("advice")
    val advice: String? = null,

    /**
     * Digest of certificate of the APK that submitted this request.
     */
    @SerializedName("apkCertificateDigestSha256")
    val apkCertificateDigestSha256: Array<String>? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttestationData

        if (nonce != other.nonce) return false
        if (timestampMs != other.timestampMs) return false
        if (apkPackageName != other.apkPackageName) return false
        if (apkDigestSha256 != other.apkDigestSha256) return false
        if (ctsProfileMatch != other.ctsProfileMatch) return false
        if (basicIntegrity != other.basicIntegrity) return false
        if (advice != other.advice) return false
        if (apkCertificateDigestSha256 != null) {
            if (other.apkCertificateDigestSha256 == null) return false
            if (!apkCertificateDigestSha256.contentEquals(other.apkCertificateDigestSha256)) return false
        } else if (other.apkCertificateDigestSha256 != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nonce?.hashCode() ?: 0
        result = 31 * result + timestampMs.hashCode()
        result = 31 * result + (apkPackageName?.hashCode() ?: 0)
        result = 31 * result + (apkDigestSha256?.hashCode() ?: 0)
        result = 31 * result + ctsProfileMatch.hashCode()
        result = 31 * result + basicIntegrity.hashCode()
        result = 31 * result + (advice?.hashCode() ?: 0)
        result = 31 * result + (apkCertificateDigestSha256?.contentHashCode() ?: 0)
        return result
    }
}
