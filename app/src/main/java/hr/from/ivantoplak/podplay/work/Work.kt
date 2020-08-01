package hr.from.ivantoplak.podplay.work

import androidx.work.WorkRequest
import java.util.*

interface Work {

    fun schedule(workRequest: WorkRequest)

    fun scheduleUniqueWork(uniqueWorkName: String, workRequest: WorkRequest)

    fun cancel(workId: UUID)

    fun cancelUniqueWork(uniqueWorkName: String)
}