package hr.from.ivantoplak.podplay.repository

import hr.from.ivantoplak.podplay.model.Podcast
import hr.from.ivantoplak.podplay.model.PodcastUpdateInfo
import kotlinx.coroutines.flow.Flow

interface PodcastRepo {

    suspend fun getPodcast(feedUrl: String): Podcast
    suspend fun savePodcast(podcast: Podcast)
    fun getAllPodcasts(): Flow<List<Podcast>>
    suspend fun deletePodcast(podcast: Podcast)
    suspend fun updatePodcastEpisodes(): List<PodcastUpdateInfo>
}