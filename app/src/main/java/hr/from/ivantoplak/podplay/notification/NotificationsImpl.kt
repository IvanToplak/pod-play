package hr.from.ivantoplak.podplay.notification

import android.app.Notification
import androidx.core.app.NotificationManagerCompat
import javax.inject.Inject

class NotificationsImpl @Inject constructor(private val notificationManagerCompat: NotificationManagerCompat) :
    Notifications {

    override fun show(notificationId: Int, notification: Notification) =
        notificationManagerCompat.notify(notificationId, notification)

    override fun update(notificationId: Int, notification: Notification) =
        notificationManagerCompat.notify(notificationId, notification)

    override fun remove(notificationId: Int) = notificationManagerCompat.cancel(notificationId)

    override fun removeAll() = notificationManagerCompat.cancelAll()
}