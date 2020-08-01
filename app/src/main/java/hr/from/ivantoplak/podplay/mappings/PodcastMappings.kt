package hr.from.ivantoplak.podplay.mappings

import hr.from.ivantoplak.podplay.db.DbPodcast
import hr.from.ivantoplak.podplay.model.Podcast
import hr.from.ivantoplak.podplay.model.PodcastSummaryViewData
import hr.from.ivantoplak.podplay.model.PodcastViewData
import hr.from.ivantoplak.podplay.service.network.RssFeedResponse

fun List<DbPodcast>.toPodcasts(): List<Podcast> = map { it.toPodcast() }

fun List<Podcast>.toPodcastSummaryViews(): List<PodcastSummaryViewData> =
    map { it.toPodcastSummaryView() }

fun DbPodcast.toPodcast(): Podcast = Podcast(
    id = id,
    feedUrl = feedUrl,
    feedTitle = feedTitle,
    feedDesc = feedDesc,
    imageUrl = imageUrl,
    artworkUrl = artworkUrl,
    category = category
)

fun Podcast.toPodcastView(): PodcastViewData = PodcastViewData(
    subscribed = id > 0,
    feedTitle = feedTitle,
    feedUrl = feedUrl,
    feedDesc = feedDesc,
    imageUrl = imageUrl,
    artworkUrl = artworkUrl,
    category = category,
    episodes = episodes.toEpisodeViews()
)

fun Podcast.toDbPodcast(): DbPodcast = DbPodcast(
    id = id,
    feedUrl = feedUrl,
    feedTitle = feedTitle,
    feedDesc = feedDesc,
    imageUrl = imageUrl,
    artworkUrl = artworkUrl,
    category = category
)

fun RssFeedResponse.toPodcast(feedUrl: String, imageUrl: String, artworkUrl: String): Podcast =
    Podcast(
        feedUrl = feedUrl,
        feedTitle = title,
        feedDesc = if (description.isBlank()) summary else description,
        imageUrl = imageUrl,
        artworkUrl = artworkUrl,
        category = category,
        episodes = episodes.responseToEpisodes()
    )

fun Podcast.toPodcastSummaryView(): PodcastSummaryViewData = PodcastSummaryViewData(
    name = feedTitle,
    primaryGenreName = category,
    imageUrl = imageUrl,
    artworkUrl = artworkUrl,
    feedUrl = feedUrl
)

