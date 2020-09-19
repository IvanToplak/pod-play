package hr.from.ivantoplak.podplay.router

import androidx.fragment.app.FragmentManager
import hr.from.ivantoplak.podplay.R
import hr.from.ivantoplak.podplay.ui.AudioPlayerFragment
import hr.from.ivantoplak.podplay.ui.EpisodeDetailsFragment
import hr.from.ivantoplak.podplay.ui.PodcastDetailsFragment
import hr.from.ivantoplak.podplay.ui.VideoPlayerFragment
import javax.inject.Inject

class RouterImpl @Inject constructor(private val fragmentManager: FragmentManager) : Router {

    override fun showPodcastDetailsScreen() {
        val fragment =
            fragmentManager.findFragmentByTag(PodcastDetailsFragment.TAG) as? PodcastDetailsFragment
        if (fragment == null) {
            fragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .add(
                    R.id.podcastDetailsContainer,
                    PodcastDetailsFragment.newInstance(),
                    PodcastDetailsFragment.TAG
                )
                .commit()
        } else {
            fragment.updateScreen()
        }
    }

    override fun hidePodcastDetailsScreen() {
        val fragment = fragmentManager.findFragmentByTag(PodcastDetailsFragment.TAG)
        fragment?.let { fragmentManager.popBackStack() }
    }

    override fun showEpisodeDetailsScreen() {
        val fragment = fragmentManager.findFragmentByTag(EpisodeDetailsFragment.TAG)
        if (fragment == null) {
            fragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .add(
                    R.id.podcastDetailsContainer,
                    EpisodeDetailsFragment.newInstance(),
                    EpisodeDetailsFragment.TAG
                )
                .commit()
        }
    }

    override fun hideEpisodeDetailsScreen() {
        val fragment = fragmentManager.findFragmentByTag(EpisodeDetailsFragment.TAG)
        fragment?.let { fragmentManager.popBackStack() }
    }

    override fun showVideoPlayer(episodeUrl: String) {
        val fragment = fragmentManager.findFragmentByTag(VideoPlayerFragment.TAG)
        if (fragment == null) {
            fragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .add(
                    R.id.podcastDetailsContainer,
                    VideoPlayerFragment.newInstance(episodeUrl),
                    VideoPlayerFragment.TAG
                )
                .commit()
        }
    }

    override fun showAudioPlayer() {
        val fragment = fragmentManager.findFragmentByTag(AudioPlayerFragment.TAG)
        if (fragment == null) {
            fragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .add(
                    R.id.podcastDetailsContainer,
                    AudioPlayerFragment.newInstance(),
                    AudioPlayerFragment.TAG
                )
                .commit()
        }
    }
}