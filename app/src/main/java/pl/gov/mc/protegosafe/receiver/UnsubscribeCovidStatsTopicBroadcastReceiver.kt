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
import pl.gov.mc.protegosafe.worker.UnsubscribeCovidStatsTopicWorker
import timber.log.Timber

class UnsubscribeCovidStatsTopicBroadcastReceiver : BroadcastReceiver(), KoinComponent {

    private val workManager: WorkManager by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Consts.ACTION_UNSUBSCRIBE_COVID_STATS_TOPIC) {
            intent.getIntExtra(Consts.COVID_STATS_NOTIIFICATION_EXTRA_ID, 0).let { id ->
                Timber.d("UnsubscribeCovidStatsTopicBroadcastReceiver: notification id = $id")
                workManager.enqueue(
                    OneTimeWorkRequest.Builder(UnsubscribeCovidStatsTopicWorker::class.java)
                        .setInputData(
                            Data.Builder()
                                .putInt(Consts.COVID_STATS_NOTIIFICATION_EXTRA_ID, id)
                                .build()
                        )
                        .build()
                )
            }
        }
    }
}
