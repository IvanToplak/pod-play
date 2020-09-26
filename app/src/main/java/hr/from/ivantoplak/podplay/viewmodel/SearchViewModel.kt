package hr.from.ivantoplak.podplay.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import hr.from.ivantoplak.podplay.coroutines.CoroutineContextProvider
import hr.from.ivantoplak.podplay.mappings.toPodcastSummaryViews
import hr.from.ivantoplak.podplay.model.PodcastSummaryViewData
import hr.from.ivantoplak.podplay.repository.ItunesRepo
import kotlinx.coroutines.withContext

class SearchViewModel @ViewModelInject constructor(
    private val itunesRepo: ItunesRepo,
    private val coroutineContextProvider: CoroutineContextProvider
) :
    ViewModel() {

    var searchQuery = ""
    private val searchPodcastResults = mutableListOf<PodcastSummaryViewData>()

    suspend fun searchPodcasts(term: String): List<PodcastSummaryViewData> =
        withContext(coroutineContextProvider.io()) {
            if (searchQuery == term && searchPodcastResults.isNotEmpty()) return@withContext searchPodcastResults
            searchQuery = term
            searchPodcastResults.clear()
            searchPodcastResults.addAll(itunesRepo.searchByTerm(term).toPodcastSummaryViews())
            return@withContext searchPodcastResults
        }
}