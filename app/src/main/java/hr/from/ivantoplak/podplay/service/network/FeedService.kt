package hr.from.ivantoplak.podplay.service.network

interface FeedService {

    suspend fun getFeed(xmlFileURL: String): RssFeedResponse
}