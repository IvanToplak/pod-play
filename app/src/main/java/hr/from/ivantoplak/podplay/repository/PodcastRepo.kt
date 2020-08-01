package hr.from.ivantoplak.podplay.repository

import androidx.lifecycle.LiveData
import hr.from.ivantoplak.podplay.model.Podcast
import hr.from.ivantoplak.podplay.model.PodcastUpdateInfo

interface PodcastRepo {

    fun getPodcast(feedUrl: String, callback: (Podcast) -> Unit)
    fun save(podcast: Podcast)
    fun getAll(): LiveData<List<Podcast>>
    fun delete(podcast: Podcast)
    fun updatePodcastEpisodes(callback: (List<PodcastUpdateInfo>) -> Unit)
}