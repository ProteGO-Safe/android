package pl.gov.mc.protegosafe.helpers

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread
import pl.gov.mc.protegosafe.domain.repository.AppRepository
import pl.gov.mc.protegosafe.domain.scheduler.ApplicationTaskScheduler

class SetWorkersIntervalUseCase(
    private val appRepository: AppRepository,
    private val applicationTaskScheduler: ApplicationTaskScheduler,
    private val postExecutionThread: PostExecutionThread
) {
    fun execute(workersIntervalInMinutes: Long): Single<String> {
        return setWorkersInterval(workersIntervalInMinutes)
            .andThen(rescheduleTasks())
            .toSingle {
                "Workers interval : $workersIntervalInMinutes min"
            }
            .subscribeOn(Schedulers.io())
            .observeOn(postExecutionThread.scheduler)
    }

    private fun setWorkersInterval(workersIntervalInMinutes: Long): Completable {
        return Completable.fromAction {
            appRepository.setWorkersInterval(workersIntervalInMinutes)
        }
    }

    private fun rescheduleTasks(): Completable {
        return Completable.fromAction {
            applicationTaskScheduler.scheduleUpdateDistrictsRestrictionsTask()
            applicationTaskScheduler.scheduleProvideDiagnosisKeysTask()
        }
    }
}
