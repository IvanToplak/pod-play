package hr.from.ivantoplak.podplay.mappings

import hr.from.ivantoplak.podplay.model.PodcastSummaryViewData
import hr.from.ivantoplak.podplay.service.network.ItunesPodcast

fun List<ItunesPodcast>.toPodcastSummaryViews(): List<PodcastSummaryViewData> =
    map { it.toPodcastSummaryView() }

fun ItunesPodcast.toPodcastSummaryView(): PodcastSummaryViewData = PodcastSummaryViewData(
    name = collectionCensoredName,
    primaryGenreName = primaryGenreName,
    imageUrl = artworkUrl100,
    artworkUrl = artworkUrl600,
    feedUrl = feedUrl
)
