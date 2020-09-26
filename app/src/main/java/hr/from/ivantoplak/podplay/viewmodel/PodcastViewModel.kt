package hr.from.ivantoplak.podplay.viewmodel

import android.os.Build
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import hr.from.ivantoplak.podplay.coroutines.CoroutineContextProvider
import hr.from.ivantoplak.podplay.extensions.*
import hr.from.ivantoplak.podplay.mappings.toPodcastSummaryView
import hr.from.ivantoplak.podplay.mappings.toPodcastSummaryViews
import hr.from.ivantoplak.podplay.mappings.toPodcastView
import hr.from.ivantoplak.podplay.model.EpisodeViewData
import hr.from.ivantoplak.podplay.model.Podcast
import hr.from.ivantoplak.podplay.model.PodcastSummaryViewData
import hr.from.ivantoplak.podplay.model.PodcastViewData
import hr.from.ivantoplak.podplay.repository.PodcastRepo
import hr.from.ivantoplak.podplay.service.media.MetadataProvider
import hr.from.ivantoplak.podplay.service.media.PodplayMediaServiceConnection
import hr.from.ivantoplak.podplay.work.EpisodeUpdateScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

enum class PodcastViewState {
    SEARCH, //show search results
    SUBSCRIPTION //show subscriptions
}

private const val TAG = "PodcastViewModel"

class PodcastViewModel @ViewModelInject constructor(
    private val podcastRepo: PodcastRepo,
    private val episodeUpdateScheduler: EpisodeUpdateScheduler,
    private val podplayMediaServiceConnection: PodplayMediaServiceConnection,
    private val metadataProvider: MetadataProvider,
    private val coroutineContextProvider: CoroutineContextProvider
) :
    ViewModel() {

    var podcastViewState = PodcastViewState.SUBSCRIPTION
    var activePodcastViewData: PodcastViewData? = null
        private set

    var subscribedPodcasts = emptyList<PodcastSummaryViewData>()
    val podcasts: Flow<List<PodcastSummaryViewData>> by lazy { getPodcastsFlow() }

    var activeEpisodeViewData: EpisodeViewData? = null

    private var activePodcast: Podcast? = null

    fun isVideoEpisode() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        activeEpisodeViewData?.isVideo ?: false
    } else {
        false
    }

    suspend fun getPodcast(podcastSummaryViewData: PodcastSummaryViewData): PodcastViewData =
        withContext(coroutineContextProvider.io()) {
            val podcast = podcastRepo.getPodcast(podcastSummaryViewData.feedUrl)
            val pod = podcast.copy(
                feedTitle = podcastSummaryViewData.name,
                imageUrl = podcastSummaryViewData.imageUrl,
                artworkUrl = podcastSummaryViewData.artworkUrl
            )
            activePodcast = pod
            activePodcastViewData = pod.toPodcastView()
            activePodcastViewData!!
        }

    suspend fun saveActivePodcast() = withContext(coroutineContextProvider.io()) {
        activePodcast?.let {
            podcastRepo.savePodcast(it)
        }
    }

    private fun getPodcastsFlow(): Flow<List<PodcastSummaryViewData>> =
        podcastRepo.getAllPodcasts().map { podcasts -> podcasts.toPodcastSummaryViews() }
            .flowOn(coroutineContextProvider.io())

    suspend fun deleteActivePodcast() {
        activePodcast?.let {
            podcastRepo.deletePodcast(it)
        }
    }

    fun scheduleEpisodeUpdateJob() = episodeUpdateScheduler.scheduleEpisodeBackgroundUpdates()

    suspend fun setActivePodcast(feedUrl: String): PodcastSummaryViewData =
        withContext(coroutineContextProvider.io()) {
            val podcast = podcastRepo.getPodcast(feedUrl)
            activePodcast = podcast
            activePodcastViewData = podcast.toPodcastView()
            podcast.toPodcastSummaryView()
        }

    /**
     * This method takes a media url and does one of the following:
     * - If the item is *not* the active item, then play it directly.
     * - If the item *is* the active item, check whether "pause" is a permitted command. If it is,
     *   then pause playback, otherwise send "play" to resume playback.
     */
    fun playMedia(mediaUrl: String, pauseAllowed: Boolean = true) {
        val nowPlaying = podplayMediaServiceConnection.nowPlaying.value
        val transportControls = podplayMediaServiceConnection.transportControls

        val isPrepared = podplayMediaServiceConnection.playbackState.value?.isPrepared ?: false
        if (isPrepared && mediaUrl == nowPlaying?.mediaUri?.toString()) {
            podplayMediaServiceConnection.playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying ->
                        if (pauseAllowed) transportControls.pause() else Unit
                    playbackState.isPlayEnabled -> transportControls.play()
                    else -> {
                        Log.w(
                            TAG, "Playable item clicked but neither play nor pause are enabled!" +
                                    " (mediaUrl=$mediaUrl)"
                        )
                    }
                }
            }
        } else {
            metadataProvider.setMetadata(
                mediaUri = mediaUrl,
                title = activeEpisodeViewData?.title ?: "",
                artist = activePodcastViewData?.feedTitle ?: "",
                artUri = activePodcastViewData?.artworkUrl ?: ""
            )
            transportControls.playFromUri(mediaUrl.toUri(), null)
        }
    }

    fun stopPlayback() = podplayMediaServiceConnection.transportControls.stop()
}