package hr.from.ivantoplak.podplay.repository

import hr.from.ivantoplak.podplay.service.network.ItunesPodcast
import hr.from.ivantoplak.podplay.service.network.ItunesService
import javax.inject.Inject

class ItunesRepoImpl @Inject constructor(private val itunesService: ItunesService) : ItunesRepo {

    override suspend fun searchByTerm(term: String): List<ItunesPodcast> =
        itunesService.searchPodcastByTerm(term).results.filter { podcast ->
            podcast.feedUrl.startsWith("https")
        }
}