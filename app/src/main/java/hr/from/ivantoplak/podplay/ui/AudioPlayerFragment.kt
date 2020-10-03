package hr.from.ivantoplak.podplay.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import hr.from.ivantoplak.podplay.R
import hr.from.ivantoplak.podplay.extensions.slideRightTransition
import hr.from.ivantoplak.podplay.model.NowPlayingMetadata
import hr.from.ivantoplak.podplay.ui.common.HiltFragment
import hr.from.ivantoplak.podplay.viewmodel.AudioPlayerViewModel
import hr.from.ivantoplak.podplay.viewmodel.PodcastViewModel
import kotlinx.android.synthetic.main.fragment_audio_player.*

class AudioPlayerFragment : HiltFragment() {

    private val podcastViewModel: PodcastViewModel by activityViewModels()
    private val viewModel: AudioPlayerViewModel by viewModels()

    companion object {
        const val TAG = "AudioPlayerFragment"
        fun newInstance() = AudioPlayerFragment()
    }

    override fun doOnCreate(savedInstanceState: Bundle?) {
        enterTransition = slideRightTransition()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_audio_player, container, false)

    override fun doOnViewCreated(view: View, savedInstanceState: Bundle?) {
        setupObservers(view)
        updateControls()
        initializePlaybackIndicators()
    }

    override fun doOnDestroy() {
        if (!(activity as FragmentActivity).isChangingConfigurations) {
            podcastViewModel.stopPlayback()
        }
    }

    private fun updateControls() {
        mediaButton.setOnClickListener {
            viewModel.mediaMetadata.value?.let { podcastViewModel.playMedia(it.mediaUri.toString()) }
        }
    }

    /**
     * Attach observers to the LiveData coming from this ViewModel
     *
     */
    private fun setupObservers(view: View) {
        viewModel.mediaMetadata.observe(
            viewLifecycleOwner, { mediaItem ->
                updateUI(view, mediaItem)
            })

        viewModel.mediaButtonRes.observe(viewLifecycleOwner, { res ->
            mediaButton.setImageResource(res)
        })

        viewModel.mediaPosition.observe(viewLifecycleOwner, { pos ->
            position.text = NowPlayingMetadata.timestampToHourMinSec(pos)
        })
    }

    private fun initializePlaybackIndicators() {
        // Initialize playback duration and position to zero
        duration.text = NowPlayingMetadata.timestampToHourMinSec(0L)
        position.text = NowPlayingMetadata.timestampToHourMinSec(0L)
    }

    /**
     * Internal function used to update all UI elements except for the current item playback
     */
    private fun updateUI(view: View, metadata: NowPlayingMetadata) {
        if (metadata.albumArtUri == Uri.EMPTY) {
            albumArt.setImageResource(R.drawable.ic_album_black_24dp)
        } else {
            Glide.with(view)
                .load(metadata.albumArtUri)
                .into(albumArt)
        }
        title.text = metadata.title
        subtitle.text = metadata.subtitle
        duration.text = metadata.duration
    }
}