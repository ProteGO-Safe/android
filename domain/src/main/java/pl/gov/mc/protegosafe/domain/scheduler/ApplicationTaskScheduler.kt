package pl.gov.mc.protegosafe.domain.scheduler

interface ApplicationTaskScheduler {
    /**
     * Schedule Provide Diagnosis Keys Task.
     *
     * @param shouldReplaceExistingWork If the task already scheduled then reschedule it or keep.
     */
    fun scheduleProvideDiagnosisKeysTask(shouldReplaceExistingWork: Boolean = true)

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

    /**
     * Schedule Update Districts Restrictions Task.
     *
     * If the task already scheduled then reschedule it.
     */
    fun scheduleUpdateDistrictsRestrictionsTask()
}
