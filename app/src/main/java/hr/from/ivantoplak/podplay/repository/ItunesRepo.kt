package hr.from.ivantoplak.podplay.repository

import hr.from.ivantoplak.podplay.service.network.ItunesPodcast

interface ItunesRepo {

    fun searchByTerm(term: String, callBack: (List<ItunesPodcast>?) -> Unit)
}