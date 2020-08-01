package hr.from.ivantoplak.podplay.service.network

import hr.from.ivantoplak.podplay.extensions.containsCaseInsensitive
import hr.from.ivantoplak.podplay.extensions.transformDate
import okhttp3.*
import org.w3c.dom.Node
import java.io.IOException
import javax.inject.Inject
import javax.xml.parsers.DocumentBuilder

private const val SOURCE_DATE_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z"

class RssFeedService @Inject constructor(
    private val client: OkHttpClient,
    private val documentBuilder: DocumentBuilder
) : FeedService {

    override fun getFeed(xmlFileURL: String, callBack: (RssFeedResponse) -> Unit) {

        val request = Request.Builder().url(xmlFileURL).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callBack(RssFeedResponse())
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        val doc = documentBuilder.parse(responseBody.byteStream())
                        val rssFeedResponse =
                            RssFeedResponse()
                        domToRssFeedResponse(doc, rssFeedResponse)
                        callBack(rssFeedResponse)
                    }
                } else {
                    callBack(RssFeedResponse())
                }
            }
        })
    }

    private fun domToRssFeedResponse(node: Node, rssFeedResponse: RssFeedResponse) {
        if (node.nodeType == Node.ELEMENT_NODE) {
            val nodeName = node.nodeName
            val parentName = node.parentNode.nodeName
            val grandParentName = node.parentNode.parentNode?.nodeName ?: ""
            if (parentName == "item" && grandParentName == "channel" && rssFeedResponse.episodes.isNotEmpty()) {
                val currentItem = rssFeedResponse.episodes.last()
                when (nodeName) {
                    "title" -> currentItem.title = node.textContent ?: ""
                    "description" -> currentItem.description = node.textContent ?: ""
                    "itunes:duration" -> currentItem.duration = node.textContent ?: ""
                    "guid" -> currentItem.guid = node.textContent ?: ""
                    "pubDate" -> currentItem.pubDate = node.textContent.transformDate(
                        SOURCE_DATE_PATTERN
                    )
                    "link" -> currentItem.link = node.textContent ?: ""
                    "enclosure" -> {
                        currentItem.url = node.attributes.getNamedItem("url").textContent ?: ""
                        currentItem.type = node.attributes.getNamedItem("type").textContent ?: ""
                    }
                }
            }

            if (parentName == "channel") {
                when (nodeName) {
                    "title" -> rssFeedResponse.title = node.textContent ?: ""
                    "description" -> rssFeedResponse.description = node.textContent ?: ""
                    "itunes:summary" -> rssFeedResponse.summary = node.textContent ?: ""
                    "item" -> rssFeedResponse.episodes.add(EpisodeResponse())
                    "itunes:category" -> {
                        val category = node.attributes.getNamedItem("text").textContent ?: ""
                        if (category.isNotBlank())
                            if (!rssFeedResponse.category.containsCaseInsensitive(category)) {
                                rssFeedResponse.category =
                                    if (rssFeedResponse.category.isBlank()) category else "${rssFeedResponse.category}, $category"
                            }
                    }
                }
            }
        }

        val nodeList = node.childNodes
        for (i in 0 until nodeList.length) {
            val childNode = nodeList.item(i)
            domToRssFeedResponse(childNode, rssFeedResponse)
        }
    }
}