package pl.gov.mc.protegosafe.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import org.koin.core.KoinComponent
import org.koin.core.inject
import pl.gov.mc.protegosafe.Consts
import pl.gov.mc.protegosafe.domain.repository.ExposureNotificationRepository
import pl.gov.mc.protegosafe.worker.ExposureStateUpdateWorker
import timber.log.Timber

/**
 * Broadcast receiver for callbacks from exposure notification API.
 *
 * Receive broadcasts of the ACTION_EXPOSURE_STATE_UPDATED intent.
 * This intent is broadcast when diagnosis keys have been compared with the keys on the device,
 * which follows calls to provideDiagnosisKeys().
 * Respond to the broadcast by presenting information to the user.
 */
class ExposureNotificationBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    private val exposureNotificationRepository: ExposureNotificationRepository by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (exposureNotificationRepository.ACTION_EXPOSURE_STATE_UPDATED == intent?.action) {
            intent.getStringExtra(exposureNotificationRepository.EXTRA_TOKEN)?.let { token ->
                Timber.d("Exposure notification state updated")
                val workManager: WorkManager by inject()
                workManager.enqueue(
                    OneTimeWorkRequest.Builder(ExposureStateUpdateWorker::class.java)
                        .setInputData(
                            Data.Builder()
                                .putString(Consts.EXPOSURE_STATE_UPDATED_EXTRA_TOKEN, token)
                                .build()
                        )
                        .build()
                )
            }
        }
    }
}
