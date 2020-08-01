package hr.from.ivantoplak.podplay.repository

import hr.from.ivantoplak.podplay.service.network.ItunesPodcast
import hr.from.ivantoplak.podplay.service.network.ItunesService
import hr.from.ivantoplak.podplay.service.network.PodcastResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ItunesRepoImpl @Inject constructor(private val itunesService: ItunesService) : ItunesRepo {

    override fun searchByTerm(term: String, callBack: (List<ItunesPodcast>?) -> Unit) {
        val podcastCall = itunesService.searchPodcastByTerm(term)
        podcastCall.enqueue(object : Callback<PodcastResponse> {
            override fun onFailure(call: Call<PodcastResponse>?, t: Throwable?) {
                callBack(emptyList())
            }

            override fun onResponse(
                call: Call<PodcastResponse>?,
                response: Response<PodcastResponse>?
            ) {
                val body = response?.body()
                callBack(body?.results?.filter { podcast -> podcast.feedUrl.startsWith("https") }
                    ?: emptyList())
            }
        })
    }
}