package hr.from.ivantoplak.podplay.work

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import hr.from.ivantoplak.podplay.intent.PendingIntentFactory
import hr.from.ivantoplak.podplay.notification.NotificationFactory
import hr.from.ivantoplak.podplay.notification.Notifications
import hr.from.ivantoplak.podplay.repository.PodcastRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class EpisodeUpdateWorker @WorkerInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val podcastRepo: PodcastRepo,
    private val notifications: Notifications,
    private val notificationFactory: NotificationFactory,
    private val pendingIntentFactory: PendingIntentFactory
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = coroutineScope {
        val job = async(Dispatchers.IO) {

            podcastRepo.updatePodcastEpisodes { podcastUpdates ->

                for (podcastUpdate in podcastUpdates) {
                    val pendingIntent =
                        pendingIntentFactory.createNewEpisodesNotificationPendingIntent(
                            podcastUpdate
                        )
                    val notification = notificationFactory.createNewEpisodesNotification(
                        pendingIntent,
                        podcastUpdate
                    )
                    notifications.show(podcastUpdate.id, notification)
                }
            }
        }
        job.await()
        Result.success()
    }
}