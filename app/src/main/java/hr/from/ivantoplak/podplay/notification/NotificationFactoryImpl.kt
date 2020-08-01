package hr.from.ivantoplak.podplay.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import hr.from.ivantoplak.podplay.R
import hr.from.ivantoplak.podplay.model.PodcastUpdateInfo
import javax.inject.Inject

private const val NEW_EPISODES_NOTIFICATION_CHANNEL_ID = "hr.from.ivantoplak.podplay.NEW_EPISODES"

class NotificationFactoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManagerCompat: NotificationManagerCompat
) : NotificationFactory {

    override fun createNewEpisodesNotification(
        contentIntent: PendingIntent,
        podcastInfo: PodcastUpdateInfo
    ): Notification {
        createNewEpisodesNotificationChannel()
        return createNewEpisodesNotificationInternal(contentIntent, podcastInfo)
    }

    private fun createNewEpisodesNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NEW_EPISODES_NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.new_episodes_notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description =
                    context.getString(R.string.new_episodes_notification_channel_description)
            }
            notificationManagerCompat.createNotificationChannel(channel)
        }
    }

    private fun createNewEpisodesNotificationInternal(
        contentIntent: PendingIntent,
        podcastInfo: PodcastUpdateInfo
    ) =
        NotificationCompat.Builder(context, NEW_EPISODES_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_episode_icon)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
            .setContentTitle(context.getString(R.string.new_episodes_notification_title))
            .setContentText(
                context.getString(
                    R.string.new_episodes_notification_text,
                    podcastInfo.newCount,
                    podcastInfo.name
                )
            )
            .setNumber(podcastInfo.newCount)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .build()
}