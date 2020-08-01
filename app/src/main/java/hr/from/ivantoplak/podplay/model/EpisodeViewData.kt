package hr.from.ivantoplak.podplay.model

data class EpisodeViewData(
    val guid: String = "",
    val title: String = "",
    val description: String = "",
    val mediaUrl: String = "",
    val releaseDate: String = "",
    val duration: String = "",
    val isVideo: Boolean = false
)