package hr.from.ivantoplak.podplay.repository

import hr.from.ivantoplak.podplay.service.network.ItunesPodcast

interface ItunesRepo {

    suspend fun searchByTerm(term: String): List<ItunesPodcast>
}