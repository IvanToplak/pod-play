package hr.from.ivantoplak.podplay.intent

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import hr.from.ivantoplak.podplay.model.PodcastUpdateInfo
import hr.from.ivantoplak.podplay.ui.PodcastActivity
import javax.inject.Inject

const val EXTRA_FEED_URL = "PodcastFeedUrl"

class PendingIntentFactoryImpl @Inject constructor(@ApplicationContext private val context: Context) :
    PendingIntentFactory {

    override fun createNewEpisodesNotificationPendingIntent(podcastInfo: PodcastUpdateInfo): PendingIntent {
        val podcastActivityIntent = Intent(context, PodcastActivity::class.java)
        podcastActivityIntent.putExtra(EXTRA_FEED_URL, podcastInfo.feedUrl)
        return PendingIntent.getActivity(
            context,
            podcastInfo.id,
            podcastActivityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}