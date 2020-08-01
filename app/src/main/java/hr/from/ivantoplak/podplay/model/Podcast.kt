package hr.from.ivantoplak.podplay.model

data class Podcast(
    val id: Int = 0,
    val feedUrl: String = "",
    val feedTitle: String = "",
    val feedDesc: String = "",
    val imageUrl: String = "",
    val artworkUrl: String = "",
    val category: String = "",
    val episodes: MutableList<Episode> = mutableListOf()
)