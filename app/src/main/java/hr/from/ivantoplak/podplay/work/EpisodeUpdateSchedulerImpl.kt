package hr.from.ivantoplak.podplay.work

import androidx.work.WorkRequest
import javax.inject.Inject

private const val TAG_EPISODE_UPDATE_JOB = "hr.from.ivantoplak.podplay.episode_update"

class EpisodeUpdateSchedulerImpl @Inject constructor(
    private val episodeUpdateWorkRequest: WorkRequest,
    private val work: Work
) : EpisodeUpdateScheduler {
    override fun scheduleEpisodeBackgroundUpdates() =
        work.scheduleUniqueWork(TAG_EPISODE_UPDATE_JOB, episodeUpdateWorkRequest)

    override fun cancelEpisodeBackgroundUpdates() = work.cancelUniqueWork(TAG_EPISODE_UPDATE_JOB)
}
