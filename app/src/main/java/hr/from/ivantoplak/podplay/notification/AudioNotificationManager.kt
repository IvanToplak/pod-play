package hr.from.ivantoplak.podplay.notification

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import hr.from.ivantoplak.podplay.R
import kotlinx.coroutines.*
import java.net.URL

private const val NOW_PLAYING_CHANNEL_ID = "hr.from.ivantoplak.podplay.NOW_PLAYING"
private const val NOW_PLAYING_NOTIFICATION_ID = 0xb339
private const val REWIND_INCREMENT_MS = 10000L
private const val FAST_FORWARD_INCREMENT_MS = 30000L

/**
 * A wrapper class for ExoPlayer's PlayerNotificationManager. It sets up the notification shown to
 * the user during audio playback and provides track metadata, such as track title and icon image.
 */
class AudioNotificationManager(
    private val context: Context,
    private val player: ExoPlayer,
    sessionToken: MediaSessionCompat.Token,
    notificationListener: PlayerNotificationManager.NotificationListener
) {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private val notificationManager by lazy {
        val mediaController = MediaControllerCompat(context, sessionToken)
        PlayerNotificationManager.createWithNotificationChannel(
            context,
            NOW_PLAYING_CHANNEL_ID,
            R.string.notification_channel,
            R.string.notification_channel_description,
            NOW_PLAYING_NOTIFICATION_ID,
            DescriptionAdapter(mediaController),
            notificationListener
        ).apply {
            setUseNavigationActions(false)
            setUseNavigationActionsInCompactView(false)
            setMediaSessionToken(sessionToken)
            setSmallIcon(R.drawable.ic_episode_icon)
            setRewindIncrementMs(REWIND_INCREMENT_MS)
            setFastForwardIncrementMs(FAST_FORWARD_INCREMENT_MS)
        }
    }

    fun hideNotification() {
        notificationManager.setPlayer(null)
    }

    fun showNotification() {
        notificationManager.setPlayer(player)
    }

    private inner class DescriptionAdapter(private val controller: MediaControllerCompat) :
        PlayerNotificationManager.MediaDescriptionAdapter {

        var currentIconUri: Uri? = null
        var currentBitmap: Bitmap? = null

        override fun createCurrentContentIntent(player: Player): PendingIntent? =
            controller.sessionActivity

        override fun getCurrentContentText(player: Player) =
            controller.metadata.description.subtitle.toString()

        override fun getCurrentContentTitle(player: Player) =
            controller.metadata.description.title.toString()

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            val iconUri = controller.metadata.description.iconUri
            return if (currentIconUri != iconUri || currentBitmap == null) {

                // Cache the bitmap for the current song so that successive calls to
                // `getCurrentLargeIcon` don't cause the bitmap to be recreated.
                currentIconUri = iconUri
                serviceScope.launch {
                    currentBitmap = iconUri?.let {
                        resolveUriAsBitmap(it)
                    }
                    currentBitmap?.let { callback.onBitmap(it) }
                }
                null
            } else {
                currentBitmap
            }
        }

        private suspend fun resolveUriAsBitmap(uri: Uri): Bitmap? {
            return withContext(Dispatchers.IO) {
                try {
                    val iconUrl = URL(uri.toString())
                    BitmapFactory.decodeStream(iconUrl.openStream())
                } catch (ex: Exception) {
                    return@withContext null
                }
            }
        }
    }
}
