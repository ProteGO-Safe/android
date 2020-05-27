package pl.gov.mc.protegosafe.domain.scheduler

interface ApplicationTaskScheduler {
    /**
     * Schedule Provide Diagnosis Keys Task.
     *
     * If the task already scheduled then reschedule it.
     */
    fun scheduleProvideDiagnosisKeysTask()

    /**
     * Cancel Provide Diagnosis Keys Task.
     */
    fun cancelProvideDiagnosisKeysTask()

    /**
     * Schedule Remove Old Exposures Task.
     *
     * If the task already scheduled then do nothing.
     */
    fun scheduleRemoveOldExposuresTask()
}
