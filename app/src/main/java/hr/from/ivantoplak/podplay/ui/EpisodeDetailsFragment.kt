package hr.from.ivantoplak.podplay.ui

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import hr.from.ivantoplak.podplay.R
import hr.from.ivantoplak.podplay.extensions.htmlToSpannable
import hr.from.ivantoplak.podplay.extensions.slideRightTransition
import hr.from.ivantoplak.podplay.extensions.toHourMinSec
import hr.from.ivantoplak.podplay.ui.common.HiltFragment
import hr.from.ivantoplak.podplay.viewmodel.PodcastViewModel
import kotlinx.android.synthetic.main.fragment_episode_details.*

class EpisodeDetailsFragment : HiltFragment() {

    companion object {
        const val TAG = "EpisodeDetailsFragment"
        fun newInstance(): EpisodeDetailsFragment = EpisodeDetailsFragment()
    }

    private val podcastViewModel: PodcastViewModel by activityViewModels()

    override fun doOnCreate(savedInstanceState: Bundle?) {
        enterTransition = slideRightTransition()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_episode_details, container, false)

    override fun doOnActivityCreated(savedInstanceState: Bundle?) {
        setupControls()
        updateControls()
    }

    private fun setupControls() {
        episodePlayButton.setOnClickListener {
            podcastViewModel.activeEpisodeViewData?.mediaUrl?.let { episodeUrl ->
                if (podcastViewModel.isVideoEpisode()) {
                    router.showVideoPlayer(episodeUrl)
                } else {
                    podcastViewModel.playMedia(episodeUrl, false)
                    router.showAudioPlayer()
                }
            }
        }
    }

    private fun updateControls() {
        updateEpisodeDetails()
    }

    private fun updateEpisodeDetails() {
        feedTitleTextView.text = podcastViewModel.activePodcastViewData?.feedTitle ?: ""
        episodeTitleTextView.text = podcastViewModel.activeEpisodeViewData?.title ?: ""
        episodePlayButton.text = getString(
            R.string.play_episode_duration,
            podcastViewModel.activeEpisodeViewData?.duration.toHourMinSec()
        )
        val htmlDesc = podcastViewModel.activeEpisodeViewData?.description ?: ""
        episodeDescTextView.text = htmlDesc.htmlToSpannable()
        episodeDescTextView.movementMethod = LinkMovementMethod.getInstance()
        Glide.with(this).load(podcastViewModel.activePodcastViewData?.imageUrl)
            .into(episodeImageView)
    }
}