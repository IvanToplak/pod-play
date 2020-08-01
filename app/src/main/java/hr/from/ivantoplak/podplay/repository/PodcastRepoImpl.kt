package hr.from.ivantoplak.podplay.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import hr.from.ivantoplak.podplay.db.PodcastDao
import hr.from.ivantoplak.podplay.mappings.*
import hr.from.ivantoplak.podplay.model.Episode
import hr.from.ivantoplak.podplay.model.Podcast
import hr.from.ivantoplak.podplay.model.PodcastUpdateInfo
import hr.from.ivantoplak.podplay.service.network.FeedService
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject

class PodcastRepoImpl @Inject constructor(
    private val feedService: FeedService,
    private val podcastDao: PodcastDao
) : PodcastRepo {

    override fun getPodcast(feedUrl: String, callback: (Podcast) -> Unit) {
        val podcast = podcastDao.loadPodcast(feedUrl)?.toPodcast()
        if (podcast != null) {
            val episodes = podcastDao.loadEpisodes(podcast.id).toEpisodes()
            callback(podcast.copy(episodes = episodes))
        } else {
            feedService.getFeed(feedUrl) { feedResponse ->
                val pod = feedResponse.toPodcast(feedUrl, "", "")
                callback(pod)
            }
        }
    }

    override fun save(podcast: Podcast) {
        val podcastId = podcastDao.insertPodcast(podcast.toDbPodcast()).toInt()
        podcastDao.insertEpisodes(podcast.episodes.toDBEpisodes(podcastId))
    }

    override fun getAll(): LiveData<List<Podcast>> {
        val dbPodcastsLiveData = podcastDao.loadPodcasts()
        return Transformations.map(dbPodcastsLiveData) { dbPodcasts -> dbPodcasts.toPodcasts() }
    }

    override fun delete(podcast: Podcast) = podcastDao.deletePodcast(podcast.toDbPodcast())

    private fun getNewEpisodes(localPodcast: Podcast, callBack: (List<Episode>) -> Unit) {
        feedService.getFeed(localPodcast.feedUrl) { response ->
            if (response.isInvalid()) {
                callBack(emptyList())
            } else {
                val localEpisodes = podcastDao.loadEpisodes(localPodcast.id).toEpisodes()
                val newEpisodes =
                    response.episodes.responseToEpisodes(localPodcast.id).minus(localEpisodes)
                callBack(newEpisodes)
            }
        }
    }

    private fun saveNewEpisodes(episodes: List<Episode>) =
        podcastDao.insertEpisodes(episodes.toDBEpisodes())

    override fun updatePodcastEpisodes(callback: (List<PodcastUpdateInfo>) -> Unit) {
        val updatedPodcasts: MutableList<PodcastUpdateInfo> = mutableListOf()
        val podcasts = podcastDao.loadPodcastsStatic().toPodcasts()
        val processCount = AtomicInteger(podcasts.count())

        for (podcast in podcasts) {
            getNewEpisodes(podcast) { newEpisodes ->
                if (newEpisodes.count() > 0) {
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
                if (processCount.decrementAndGet() == 0) {
                    callback(updatedPodcasts)
                }
            }
        }
    }
}
