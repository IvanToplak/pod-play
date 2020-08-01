package hr.from.ivantoplak.podplay.model

import android.net.Uri
import android.text.format.DateUtils
import kotlin.math.floor

/**
 * Utility class used to represent the metadata necessary to display the
 * media item currently being played.
 */
data class NowPlayingMetadata(
    val mediaUri: Uri,
    val albumArtUri: Uri,
    val title: String?,
    val subtitle: String?,
    val duration: String
) {

    companion object {
        /**
         * Utility method to convert milliseconds to string formatted as "MM:SS" or "H:MM:SS"
         */
        private const val DURATION_UNKNOWN = "--:--"

        fun timestampToHourMinSec(position: Long): String {
            if (position < 0) return DURATION_UNKNOWN
            val totalSeconds = floor(position / 1E3).toLong()
            return DateUtils.formatElapsedTime(totalSeconds)
        }
    }
}