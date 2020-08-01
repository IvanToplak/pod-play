package hr.from.ivantoplak.podplay.viewmodel

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import hr.from.ivantoplak.podplay.mappings.toPodcastSummaryViews
import hr.from.ivantoplak.podplay.model.PodcastSummaryViewData
import hr.from.ivantoplak.podplay.repository.ItunesRepo

private const val TAG = "SearchViewModel"
private const val SEARCH_PODCASTS_ERROR_MESSAGE = "Error searching podcast on remote API"

class SearchViewModel @ViewModelInject constructor(private val itunesRepo: ItunesRepo) :
    ViewModel() {

    var searchQuery: String = ""

    fun searchPodcasts(term: String, callback: (List<PodcastSummaryViewData>) -> Unit) {
        try {
            itunesRepo.searchByTerm(term) { results ->
                callback(results?.toPodcastSummaryViews() ?: emptyList())
            }
        } catch (ex: Exception) {
            Log.e(TAG, SEARCH_PODCASTS_ERROR_MESSAGE, ex)
        }
    }
}