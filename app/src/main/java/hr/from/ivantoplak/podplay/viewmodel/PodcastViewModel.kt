package hr.from.ivantoplak.podplay.viewmodel

import android.os.Build
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class PodcastViewState {
    SEARCH, //show search results
    SUBSCRIPTION //show subscriptions
}

private const val TAG = "PodcastViewModel"
private const val GET_PODCAST_ERROR_MESSAGE = "Error retrieving podcast from database/remote API"
private const val SAVE_PODCAST_ERROR_MESSAGE = "Error saving podcast to database"
private const val GET_PODCASTS_ERROR_MESSAGE = "Error retrieving podcasts from database"
private const val DELETE_PODCAST_ERROR_MESSAGE = "Error deleting podcast from database"
private const val SET_ACTIVE_PODCAST_ERROR_MESSAGE = "Error setting active podcast from URL"

class PodcastViewModel @ViewModelInject constructor(
    private val podcastRepo: PodcastRepo,
    private val episodeUpdateScheduler: EpisodeUpdateScheduler,
    private val podplayMediaServiceConnection: PodplayMediaServiceConnection,
    private val metadataProvider: MetadataProvider
) :
    ViewModel() {

    var podcastViewState = PodcastViewState.SUBSCRIPTION
    var activePodcastViewData: PodcastViewData? = null
        private set

    private var activePodcast: Podcast? = null
    var activeEpisodeViewData: EpisodeViewData? = null
    private val livePodcastData: LiveData<List<PodcastSummaryViewData>>? by lazy {
        loadPodcasts()
    }

    fun isVideoEpisode() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        activeEpisodeViewData?.isVideo ?: false
    } else {
        false
    }

    fun getPodcast(
        podcastSummaryViewData: PodcastSummaryViewData,
        callback: (PodcastViewData) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                podcastRepo.getPodcast(podcastSummaryViewData.feedUrl) { podcast ->
                    val pod = podcast.copy(
                        feedTitle = podcastSummaryViewData.name,
                        imageUrl = podcastSummaryViewData.imageUrl,
                        artworkUrl = podcastSummaryViewData.artworkUrl
                    )
                    activePodcastViewData = pod.toPodcastView()
                    activePodcast = pod
                    viewModelScope.launch(Dispatchers.Main) {
                        activePodcastViewData?.let { callback(it) }
                    }
                }
            } catch (ex: Exception) {
                Log.e(TAG, GET_PODCAST_ERROR_MESSAGE, ex)
            }
        }
    }

    fun saveActivePodcast() {
        activePodcast?.let {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    podcastRepo.save(it)
                } catch (ex: Exception) {
                    Log.e(TAG, SAVE_PODCAST_ERROR_MESSAGE, ex)
                }
            }
        }
    }

    fun getPodcasts(): LiveData<List<PodcastSummaryViewData>>? {
        return livePodcastData
    }

    private fun loadPodcasts(): LiveData<List<PodcastSummaryViewData>>? {
        return try {
            val liveData = podcastRepo.getAll()
            Transformations.map(liveData) { podcastList -> podcastList.toPodcastSummaryViews() }
        } catch (ex: Exception) {
            Log.e(TAG, GET_PODCASTS_ERROR_MESSAGE, ex)
            null
        }
    }

    fun deleteActivePodcast() {
        activePodcast?.let {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    podcastRepo.delete(it)
                } catch (ex: Exception) {
                    Log.e(TAG, DELETE_PODCAST_ERROR_MESSAGE, ex)
                }
            }
        }
    }

    fun scheduleEpisodeUpdateJob() = episodeUpdateScheduler.scheduleEpisodeBackgroundUpdates()

    fun setActivePodcast(feedUrl: String, callback: (PodcastSummaryViewData) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                podcastRepo.getPodcast(feedUrl) {
                    activePodcastViewData = it.toPodcastView()
                    activePodcast = it
                    val activePodcastSummaryView = it.toPodcastSummaryView()
                    viewModelScope.launch(Dispatchers.Main) {
                        callback(activePodcastSummaryView)
                    }
                }
            } catch (ex: Exception) {
                Log.e(TAG, SET_ACTIVE_PODCAST_ERROR_MESSAGE, ex)
            }
        }
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