package hr.from.ivantoplak.podplay.work

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import java.util.*
import javax.inject.Inject

class WorkImpl @Inject constructor(private val workManager: WorkManager) : Work {

    override fun schedule(workRequest: WorkRequest) {
        workManager.enqueue(workRequest)
    }

    override fun scheduleUniqueWork(uniqueWorkName: String, workRequest: WorkRequest) {
        workManager.enqueueUniquePeriodicWork(
            uniqueWorkName,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest as PeriodicWorkRequest
        )
    }

    override fun cancel(workId: UUID) {
        workManager.cancelWorkById(workId)
    }

    override fun cancelUniqueWork(uniqueWorkName: String) {
        workManager.cancelUniqueWork(uniqueWorkName)
    }
}