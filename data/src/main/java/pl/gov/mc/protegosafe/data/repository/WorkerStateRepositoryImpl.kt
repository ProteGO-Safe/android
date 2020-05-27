package pl.gov.mc.protegosafe.data.repository

import pl.gov.mc.protegosafe.data.db.SharedPreferencesDelegates
import pl.gov.mc.protegosafe.domain.repository.WorkerStateRepository

class WorkerStateRepositoryImpl(sharedPreferencesDelegates: SharedPreferencesDelegates) :
    WorkerStateRepository {

    override var shouldProvideDiagnosisKeysWorkerStartOnBoot by sharedPreferencesDelegates.booleanPref(
        SHOULD_EXPOSURE_STATE_UPDATE_WORKER_START_ON_BOOT_KEY
    )

    companion object {
        private const val SHOULD_EXPOSURE_STATE_UPDATE_WORKER_START_ON_BOOT_KEY =
            "SHOULD_EXPOSURE_STATE_UPDATE_WORKER_START_ON_BOOT_KEY"
    }
}
