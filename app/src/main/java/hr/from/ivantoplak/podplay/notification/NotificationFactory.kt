package hr.from.ivantoplak.podplay.notification

import android.app.Notification
import android.app.PendingIntent
import hr.from.ivantoplak.podplay.model.PodcastUpdateInfo

interface NotificationFactory {

    fun createNewEpisodesNotification(
        contentIntent: PendingIntent,
        podcastInfo: PodcastUpdateInfo
    ): Notification
}