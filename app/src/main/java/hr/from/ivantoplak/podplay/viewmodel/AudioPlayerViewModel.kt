package hr.from.ivantoplak.podplay.viewmodel

import android.os.Handler
import android.os.Looper
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import hr.from.ivantoplak.podplay.R
import hr.from.ivantoplak.podplay.extensions.*
import hr.from.ivantoplak.podplay.model.NowPlayingMetadata
import hr.from.ivantoplak.podplay.service.media.EMPTY_PLAYBACK_STATE
import hr.from.ivantoplak.podplay.service.media.NOTHING_PLAYING
import hr.from.ivantoplak.podplay.service.media.PodplayMediaServiceConnection

private const val POSITION_UPDATE_INTERVAL_MILLIS = 100L

class AudioPlayerViewModel @ViewModelInject constructor(
    podplayMediaServiceConnection: PodplayMediaServiceConnection
) : ViewModel() {

    private var playbackState: PlaybackStateCompat = EMPTY_PLAYBACK_STATE
    val mediaMetadata = MutableLiveData<NowPlayingMetadata>()
    val mediaPosition = MutableLiveData<Long>().apply {
        postValue(0L)
    }
    val mediaButtonRes = MutableLiveData<Int>().apply {
        postValue(R.drawable.ic_album_black_24dp)
    }

    private var updatePosition = true
    private val handler = Handler(Looper.getMainLooper())

    /**
     * When the session's [PlaybackStateCompat] changes, update the state of current item (i.e.: play/pause button or blank)
     */
    private val playbackStateObserver: Observer<PlaybackStateCompat> = Observer {
        playbackState = it ?: EMPTY_PLAYBACK_STATE
        val metadata = podplayMediaServiceConnection.nowPlaying.value ?: NOTHING_PLAYING
        updateState(playbackState, metadata)
    }

    /**
     * When the session's [MediaMetadataCompat] changes, it means the currently active item has changed.
     * As a result, the new, and potentially old item (if there was one), both need to have their
     * playback state changed. (i.e.: play/pause button or blank)
     */
    private val mediaMetadataObserver: Observer<MediaMetadataCompat> = Observer {
        updateState(playbackState, it)
    }

    /**
     * Because there's a complex dance between this [ViewModel] and the [PodplayMediaServiceConnection]
     * (which is wrapping a [MediaBrowserCompat] object), the usual guidance of using
     * [Transformations] doesn't quite work.
     *
     * Specifically there's three things that are watched that will cause the single piece of
     * [LiveData] exposed from this class to be updated.
     *
     * [PodplayMediaServiceConnection.playbackState] changes state based on the playback state of
     * the player.
     *
     * [PodplayMediaServiceConnection.nowPlaying] changes based on the item that's being played.
     */
    private val musicServiceConnection = podplayMediaServiceConnection.also {
        it.playbackState.observeForever(playbackStateObserver)
        it.nowPlaying.observeForever(mediaMetadataObserver)
        checkPlaybackPosition()
    }

    /**
     * Since we use [LiveData.observeForever] above (in [musicServiceConnection]), we want
     * to call [LiveData.removeObserver] here to prevent leaking resources when the [ViewModel]
     * is not longer in use.
     */
    override fun onCleared() {
        super.onCleared()

        // Remove the permanent observers from the MusicServiceConnection.
        musicServiceConnection.playbackState.removeObserver(playbackStateObserver)
        musicServiceConnection.nowPlaying.removeObserver(mediaMetadataObserver)

        // Stop updating the position
        updatePosition = false
    }

    /**
     * Internal function that recursively calls itself every [POSITION_UPDATE_INTERVAL_MILLIS] ms
     * to check the current playback position and updates the corresponding LiveData object when it
     * has changed.
     */
    private fun checkPlaybackPosition(): Boolean = handler.postDelayed({
        val currPosition = playbackState.currentPlayBackPosition
        if (mediaPosition.value != currPosition)
            mediaPosition.postValue(currPosition)
        if (updatePosition)
            checkPlaybackPosition()
    }, POSITION_UPDATE_INTERVAL_MILLIS)


    private fun updateState(
        playbackState: PlaybackStateCompat,
        mediaMetadata: MediaMetadataCompat
    ) {
        // Only update media item once we have duration available
        if (mediaMetadata.duration != 0L) {
            val nowPlayingMetadata = NowPlayingMetadata(
                mediaMetadata.mediaUri,
                mediaMetadata.albumArtUri,
                mediaMetadata.title?.trim(),
                mediaMetadata.displaySubtitle?.trim(),
                NowPlayingMetadata.timestampToHourMinSec(mediaMetadata.duration)
            )
            this.mediaMetadata.postValue(nowPlayingMetadata)
        }

        // Update the media button resource ID
        mediaButtonRes.postValue(
            when (playbackState.isPlaying) {
                true -> R.drawable.ic_pause_black_24dp
                else -> R.drawable.ic_play_arrow_black_24dp
            }
        )
    }
}