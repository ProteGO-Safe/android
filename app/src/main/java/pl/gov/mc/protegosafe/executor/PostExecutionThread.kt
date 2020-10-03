package pl.gov.mc.protegosafe.executor

import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import pl.gov.mc.protegosafe.domain.executor.PostExecutionThread

class PostExecutionThread : PostExecutionThread {
    override val scheduler: Scheduler
        get() = AndroidSchedulers.mainThread()
}
