package hr.from.ivantoplak.podplay.model

data class PodcastViewData(
    val subscribed: Boolean = false,
    val feedTitle: String = "",
    val feedUrl: String = "",
    val feedDesc: String = "",
    val imageUrl: String = "",
    val artworkUrl: String = "",
    val category: String = "",
    val episodes: MutableList<EpisodeViewData> = mutableListOf()
) {
    fun isValid() = feedTitle.isNotBlank()
}