package hr.from.ivantoplak.podplay.repository

import android.util.Log
import hr.from.ivantoplak.podplay.db.PodcastDao
import hr.from.ivantoplak.podplay.mappings.*
import hr.from.ivantoplak.podplay.model.Episode
import hr.from.ivantoplak.podplay.model.Podcast
import hr.from.ivantoplak.podplay.model.PodcastUpdateInfo
import hr.from.ivantoplak.podplay.service.network.FeedService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "PodcastRepoImpl"
private const val UPDATE_PODCAST_EPISODES_ERROR_MESSAGE =
    "Error while updating episodes for feed URL"

class PodcastRepoImpl @Inject constructor(
    private val feedService: FeedService,
    private val podcastDao: PodcastDao
) : PodcastRepo {

    override suspend fun getPodcast(feedUrl: String): Podcast {
        val podcast = podcastDao.loadPodcast(feedUrl)?.toPodcast()
        return if (podcast != null) {
            val episodes = podcastDao.loadEpisodes(podcast.id).toEpisodes()
            podcast.copy(episodes = episodes)
        } else {
            val feedResponse = feedService.getFeed(feedUrl)
            feedResponse.toPodcast(feedUrl, "", "")
        }
    }

    override suspend fun savePodcast(podcast: Podcast) {
        val podcastId = podcastDao.insertPodcast(podcast.toDbPodcast()).toInt()
        podcastDao.insertEpisodes(podcast.episodes.toDBEpisodes(podcastId))
    }

    override fun getAllPodcasts(): Flow<List<Podcast>> =
        podcastDao.loadPodcasts().map { dbPodcasts -> dbPodcasts.toPodcasts() }


    override suspend fun deletePodcast(podcast: Podcast) =
        podcastDao.deletePodcast(podcast.toDbPodcast())

    private suspend fun getNewEpisodes(localPodcast: Podcast): List<Episode> {
        val feedResponse = feedService.getFeed(localPodcast.feedUrl)
        val localEpisodes = podcastDao.loadEpisodes(localPodcast.id).toEpisodes()
        val responseEpisodes = feedResponse.episodes.responseToEpisodes(localPodcast.id)
        return responseEpisodes.minus(localEpisodes)
    }

    private suspend fun saveNewEpisodes(episodes: List<Episode>) =
        podcastDao.insertEpisodes(episodes.toDBEpisodes())

    override suspend fun updatePodcastEpisodes(): List<PodcastUpdateInfo> {
        val updatedPodcasts: MutableList<PodcastUpdateInfo> = mutableListOf()
        val podcasts = podcastDao.loadPodcastsStatic().toPodcasts()

        for (podcast in podcasts) {
            val result = runCatching {
                val newEpisodes = getNewEpisodes(podcast)
                if (newEpisodes.isNotEmpty()) {
                    saveNewEpisodes(newEpisodes)
                    updatedPodcasts.add(
                        PodcastUpdateInfo(
                            podcast.id,
                            podcast.feedUrl,
                            podcast.feedTitle,
                            newEpisodes.count()
                        )
                    )
                }
            }
            result.onFailure { exception ->
                Log.e(TAG, "$UPDATE_PODCAST_EPISODES_ERROR_MESSAGE: ${podcast.feedUrl}", exception)
            }
        }
        return updatedPodcasts
    }
}
