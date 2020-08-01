package hr.from.ivantoplak.podplay.work

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkRequest
import java.util.concurrent.TimeUnit

object EpisodeUpdateWorkRequestFactory {

    const val REPEAT_INTERVAL = 1L
    val REPEAT_UNIT = TimeUnit.HOURS
    private const val INITIAL_DELAY = 5L
    private val DELAY_UNIT = TimeUnit.SECONDS

    fun createWorkRequest(repeatInterval: Long, repeatIntervalTimeUnit: TimeUnit): WorkRequest {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresStorageNotLow(true)
            .setRequiresBatteryNotLow(true)
            .build()

        return PeriodicWorkRequestBuilder<EpisodeUpdateWorker>(
            repeatInterval,
            repeatIntervalTimeUnit
        ).setConstraints(constraints)
            .setInitialDelay(INITIAL_DELAY, DELAY_UNIT)
            .build()
    }
}