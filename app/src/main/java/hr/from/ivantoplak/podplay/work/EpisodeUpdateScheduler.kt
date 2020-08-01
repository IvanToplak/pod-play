package hr.from.ivantoplak.podplay.work

interface EpisodeUpdateScheduler {

    fun scheduleEpisodeBackgroundUpdates()

    fun cancelEpisodeBackgroundUpdates()
}