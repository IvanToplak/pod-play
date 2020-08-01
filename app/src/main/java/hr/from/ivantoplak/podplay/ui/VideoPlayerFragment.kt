package hr.from.ivantoplak.podplay.ui

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import hr.from.ivantoplak.podplay.R
import hr.from.ivantoplak.podplay.ui.common.BaseFragment
import hr.from.ivantoplak.podplay.viewmodel.VideoPlayerViewModel
import kotlinx.android.synthetic.main.fragment_video_player.*

class VideoPlayerFragment : BaseFragment() {

    companion object {
        const val TAG = "VideoPlayerFragment"
        private const val EPISODE_URL_KEY = "EpisodeUrl"
        fun newInstance(episodeUrl: String) =
            VideoPlayerFragment().apply {
                arguments = Bundle().apply {
                    putString(EPISODE_URL_KEY, episodeUrl)
                }
            }
    }

    private val viewModel: VideoPlayerViewModel by viewModels()

    private lateinit var episodeUrl: String
    private var player: SimpleExoPlayer? = null
    private var playbackStateListener: PlaybackStateListener? = null

    override fun doOnCreate(savedInstanceState: Bundle?) {
        arguments?.let {
            episodeUrl = it.getString(EPISODE_URL_KEY) ?: ""
        }
        playbackStateListener = PlaybackStateListener()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_video_player, container, false)

    override fun doOnStart() {
        if (Util.SDK_INT >= Build.VERSION_CODES.N) {
            initializePlayer()
        }
    }

    override fun doOnResume() {
        hideSystemUi()
        if (Util.SDK_INT < Build.VERSION_CODES.N) {
            initializePlayer()
        }
    }

    override fun doOnPause() {
        if (Util.SDK_INT < Build.VERSION_CODES.N) {
            releasePlayer()
        }
    }

    override fun doOnStop() {
        if (Util.SDK_INT >= Build.VERSION_CODES.N) {
            releasePlayer()
        }
    }

    private fun initializePlayer() {
        if (player == null) {
            val trackSelector = DefaultTrackSelector(requireContext())
            trackSelector.setParameters(
                trackSelector.buildUponParameters().setMaxVideoSizeSd()
                    .setPreferredAudioLanguage(getString(R.string.audio_language))
            )
            player =
                SimpleExoPlayer.Builder(requireContext()).setTrackSelector(trackSelector).build()
                    .apply {
                        setHandleAudioBecomingNoisy(true)
                    }
            playerView.player = player
            val uri = Uri.parse(episodeUrl)
            val mediaSource = buildMediaSource(uri)
            player?.apply {
                playWhenReady = viewModel.playWhenReady
                seekTo(viewModel.currentWindow, viewModel.playbackPosition)
                playbackStateListener?.let { addListener(it) }
                prepare(mediaSource, false, false)
            }
        }
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(requireContext(), getString(R.string.app_name))

        return when (@C.ContentType val type = Util.inferContentType(uri)) {
            C.TYPE_DASH -> DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            C.TYPE_SS -> SsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            C.TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            C.TYPE_OTHER -> ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            else -> throw IllegalStateException("Unsupported type: $type")
        }
    }

    private fun hideSystemUi() {
        playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private fun releasePlayer() {
        player?.let { pl ->
            viewModel.playWhenReady = pl.playWhenReady
            viewModel.playbackPosition = pl.currentPosition
            viewModel.currentWindow = pl.currentWindowIndex
            playbackStateListener?.let { pl.removeListener(it) }
            pl.release()
            player = null
        }
    }

    inner class PlaybackStateListener : Player.EventListener {

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            playerView.keepScreenOn = isPlaying
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            val state = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE"
            }
            Log.d(TAG, "changed state to $state playWhenReady: $playWhenReady")
        }
    }
}