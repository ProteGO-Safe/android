package pl.gov.mc.protegosafe.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
import java.io.File
import pl.gov.mc.protegosafe.domain.model.ExposureConfigurationItem
import pl.gov.mc.protegosafe.domain.model.ExposureInformationItem
import pl.gov.mc.protegosafe.domain.model.ExposureNotificationStatusItem
import pl.gov.mc.protegosafe.domain.model.ExposureSummaryItem
import pl.gov.mc.protegosafe.domain.model.exposeNotification.TemporaryExposureKeyItem

interface ExposureNotificationRepository {

    val ACTION_EXPOSURE_STATE_UPDATED: String
    val EXTRA_TOKEN: String

    /**
     * Tells Google Play services to start the broadcasting and scanning process. The first time
     * that this method is called after installation of the app or stop, it prompts Google Play
     * services to display a dialog box, where the user is asked to give permission to broadcast
     * and scan.
     */
    fun start(): Completable

    /**
     * Disables broadcasting and scanning.
     */
    fun stop(): Completable

    /**
     * Indicates if exposure notifications are running.
     */
    fun isEnabled(): Single<Boolean>

    /**
     * Takes an ExposureConfiguration object. Inserts a list of files that contain key
     * information into the on-device database. Provide the keys of confirmed cases retrieved
     * from your internet-accessible server to the Google Play service once requested from the
     * API.
     */
    fun provideDiagnosisKeys(
        files: List<File>,
        token: String,
        exposureConfigurationItem: ExposureConfigurationItem?
    ): Completable

    /**
     * @return A unique token for batch
     */
    fun generateRandomToken(): String

    /**
     * Gets TemporaryExposureKey history to be stored on the server. This should only
     * be done after proper verification is performed on the client side that the user is
     * diagnosed positive. The keys provided here will only be from previous days; keys will
     * not be released until after they are no longer an active exposure key. This shows a user
     * permission dialog for sharing and uploading data to the server.
     */
    fun getTemporaryExposureKeyHistory(): Single<List<TemporaryExposureKeyItem>>

    /**
     * @return state parsed for PWA
     */
    fun getExposureNotificationState(): Single<ExposureNotificationStatusItem>

    /**
     * Gets a summary of the exposure calculation for the token, which should match
     * the token provided in {@link #provideDiagnosisKeys}.
     */
    fun getExposureSummary(token: String): Single<ExposureSummaryItem>

    /**
     * Gets detailed information about exposures that have occurred related to the
     * provided token, which should match the token provided in
     * {@link #provideDiagnosisKeys}.
     *
     * When multiple {@link ExposureInformation} objects are returned, they can
     * be:
     * <ul>
     * <li>Multiple encounters with a single diagnosis key.
     * <li>Multiple encounters with the same device across key rotation boundaries.
     * <li>Encounters with multiple devices.
     * </ul>
     *
     * Records of calls to this method will be retained and viewable by the user.
     */
    fun getExposureInformation(token: String): Single<List<ExposureInformationItem>>
}
