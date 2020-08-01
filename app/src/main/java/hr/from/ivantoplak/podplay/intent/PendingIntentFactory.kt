package hr.from.ivantoplak.podplay.intent

import android.app.PendingIntent
import hr.from.ivantoplak.podplay.model.PodcastUpdateInfo

interface PendingIntentFactory {

    fun createNewEpisodesNotificationPendingIntent(podcastInfo: PodcastUpdateInfo): PendingIntent
}