package hr.from.ivantoplak.podplay.service.network

interface FeedService {

    fun getFeed(xmlFileURL: String, callBack: (RssFeedResponse) -> Unit)
}