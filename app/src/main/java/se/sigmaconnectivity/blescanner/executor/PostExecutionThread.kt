package se.sigmaconnectivity.blescanner.executor

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import se.sigmaconnectivity.blescanner.domain.executor.PostExecutionThread

class PostExecutionThread: PostExecutionThread {
    override val scheduler: Scheduler
        get() = AndroidSchedulers.mainThread()
}