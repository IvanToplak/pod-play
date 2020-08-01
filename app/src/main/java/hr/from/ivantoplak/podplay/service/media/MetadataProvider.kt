package hr.from.ivantoplak.podplay.service.media

import android.support.v4.media.MediaMetadataCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector

class MetadataProvider : MediaSessionConnector.MediaMetadataProvider {

    private val metadataBuilder by lazy { MediaMetadataCompat.Builder() }
    private var metadata: MediaMetadataCompat = NOTHING_PLAYING

    override fun getMetadata(player: Player): MediaMetadataCompat {
        if (player.duration > 0) {
            metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, player.duration)
            metadata = metadataBuilder.build()
        }
        return metadata
    }

    fun setMetadata(mediaUri: String ,title: String, artist: String, artUri: String) {
        metadataBuilder
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, mediaUri)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, artist)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, artUri)
            .putString(MediaMetadataCompat.METADATA_KEY_ART_URI, artUri)
    }
}