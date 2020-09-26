package hr.from.ivantoplak.podplay.service.network

import java.util.*

data class RssFeedResponse(
    var title: String = "",
    var description: String = "",
    var summary: String = "",
    var category: String = "",
    var episodes: MutableList<EpisodeResponse> = mutableListOf()
)

data class EpisodeResponse(
    var title: String = "",
    var link: String = "",
    var description: String = "",
    var guid: String = "",
    var pubDate: Date? = null,
    var duration: String = "",
    var url: String = "",
    var type: String = ""
)
