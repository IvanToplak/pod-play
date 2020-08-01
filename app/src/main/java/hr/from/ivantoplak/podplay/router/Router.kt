package hr.from.ivantoplak.podplay.router

interface Router {

    fun showPodcastDetailsScreen()
    fun hidePodcastDetailsScreen()
    fun showEpisodeDetailsScreen()
    fun hideEpisodeDetailsScreen()
    fun showVideoPlayer(episodeUrl: String)
    fun showAudioPlayer()
}