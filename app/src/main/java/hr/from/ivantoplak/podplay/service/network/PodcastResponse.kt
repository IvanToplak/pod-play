package hr.from.ivantoplak.podplay.service.network

data class PodcastResponse(
    val resultCount: Int = 0,
    val results: List<ItunesPodcast> = emptyList()
)

data class ItunesPodcast(
    val collectionCensoredName: String = "",
    val feedUrl: String = "",
    val artworkUrl100: String = "",
    val artworkUrl600: String = "",
    val primaryGenreName: String = ""
)