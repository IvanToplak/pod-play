package hr.from.ivantoplak.podplay.mappings

import hr.from.ivantoplak.podplay.db.DbEpisode
import hr.from.ivantoplak.podplay.extensions.toString
import hr.from.ivantoplak.podplay.model.Episode
import hr.from.ivantoplak.podplay.model.EpisodeViewData
import hr.from.ivantoplak.podplay.service.network.EpisodeResponse

private const val TARGET_DATE_PATTERN = "yyyy-MM-dd"

fun MutableList<Episode>.toEpisodeViews(): MutableList<EpisodeViewData> =
    map { it.toEpisodeView() }.toMutableList()

fun List<Episode>.toDBEpisodes(podcastId: Int = 0): List<DbEpisode> =
    map { it.toDbEpisode(podcastId) }

fun List<EpisodeResponse>.responseToEpisodes(podcastId: Int = 0): MutableList<Episode> =
    map { it.toEpisode(podcastId) }.toMutableList()

fun List<DbEpisode>.toEpisodes(): MutableList<Episode> = map { it.toEpisode() }.toMutableList()

fun Episode.toEpisodeView(): EpisodeViewData = EpisodeViewData(
    guid = guid,
    title = title,
    description = description,
    mediaUrl = mediaUrl,
    releaseDate = releaseDate?.toString(TARGET_DATE_PATTERN) ?: "",
    duration = duration,
    isVideo = mimeType.startsWith("video")
)

fun Episode.toDbEpisode(podcastId: Int = 0): DbEpisode = DbEpisode(
    id = id,
    guid = guid,
    podcastId = if (podcastId == 0) this.podcastId else podcastId,
    title = title,
    description = description,
    mediaUrl = mediaUrl,
    mimeType = mimeType,
    releaseDate = releaseDate,
    duration = duration
)

fun DbEpisode.toEpisode(): Episode = Episode(
    id = id,
    guid = guid,
    podcastId = podcastId,
    title = title,
    description = description,
    mediaUrl = mediaUrl,
    mimeType = mimeType,
    releaseDate = releaseDate,
    duration = duration
)

fun EpisodeResponse.toEpisode(podcastId: Int = 0): Episode = Episode(
    guid = guid,
    podcastId = podcastId,
    title = title,
    description = description,
    mediaUrl = url,
    mimeType = type,
    releaseDate = pubDate,
    duration = duration
)