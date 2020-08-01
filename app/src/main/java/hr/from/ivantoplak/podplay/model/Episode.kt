package hr.from.ivantoplak.podplay.model

import java.util.*

data class Episode(
    val id: Long = 0L,
    val guid: String = "",
    val podcastId: Int = 0,
    val title: String = "",
    val description: String = "",
    val mediaUrl: String = "",
    val mimeType: String = "",
    val releaseDate: Date? = null,
    val duration: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Episode

        if (guid != other.guid) return false

        return true
    }

    override fun hashCode(): Int {
        return guid.hashCode()
    }
}