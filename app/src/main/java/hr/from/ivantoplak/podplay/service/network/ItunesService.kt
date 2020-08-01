package hr.from.ivantoplak.podplay.service.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ItunesService {

    @GET("/search?media=podcast")
    fun searchPodcastByTerm(@Query("term") term: String): Call<PodcastResponse>
}